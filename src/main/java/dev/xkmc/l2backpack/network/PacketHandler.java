package dev.xkmc.l2backpack.network;

import dev.xkmc.l2backpack.init.LightLand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;

public enum PacketHandler {
	SLOT_CLICK_TO_SERVER(SlotClickToServer.class, PLAY_TO_SERVER);

	public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(LightLand.MODID, "main");
	public static final int NETWORK_VERSION = 1;
	public static final String NETWORK_VERSION_STR = String.valueOf(NETWORK_VERSION);
	public static SimpleChannel channel;

	private final LoadedPacket<?> packet;

	<T extends SimplePacketBase> PacketHandler(Class<T> type, Function<FriendlyByteBuf, T> factory,
											   NetworkDirection direction) {
		packet = new LoadedPacket<>(type, factory, direction);
	}

	<T extends SerialPacketBase> PacketHandler(Class<T> type, NetworkDirection direction) {
		this(type, (buf) -> SerialPacketBase.serial(type, buf), direction);
	}

	public static void registerPackets() {
		channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
				.serverAcceptedVersions(NETWORK_VERSION_STR::equals)
				.clientAcceptedVersions(NETWORK_VERSION_STR::equals)
				.networkProtocolVersion(() -> NETWORK_VERSION_STR)
				.simpleChannel();
		for (PacketHandler packet : values())
			packet.packet.register();
	}

	public static void sendToNear(Level world, BlockPos pos, int range, Object message) {
		channel.send(PacketDistributor.NEAR
				.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), range, world.dimension())), message);
	}

	private static class LoadedPacket<T extends SimplePacketBase> {
		private static int index = 0;

		private final BiConsumer<T, FriendlyByteBuf> encoder;
		private final Function<FriendlyByteBuf, T> decoder;
		private final BiConsumer<T, Supplier<NetworkEvent.Context>> handler;
		private final Class<T> type;
		private final NetworkDirection direction;

		private LoadedPacket(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
			encoder = T::write;
			decoder = factory;
			handler = T::handle;
			this.type = type;
			this.direction = direction;
		}

		private void register() {
			channel.messageBuilder(type, index++, direction)
					.encoder(encoder)
					.decoder(decoder)
					.consumer(handler)
					.add();
		}
	}

}
