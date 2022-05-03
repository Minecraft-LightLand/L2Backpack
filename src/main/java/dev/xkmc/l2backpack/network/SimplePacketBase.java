package dev.xkmc.l2backpack.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public abstract class SimplePacketBase {

	public abstract void write(FriendlyByteBuf buffer);

	public abstract void handle(Supplier<Context> context);

	public void toServer() {
		PacketHandler.channel.sendToServer(this);
	}

	public void toTrackingPlayers(Entity e) {
		PacketHandler.channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> e), this);
	}

	public void toClientPlayer(ServerPlayer e) {
		PacketHandler.channel.sendTo(this, e.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
	}

	public void toAllClient() {
		PacketHandler.channel.send(PacketDistributor.ALL.noArg(), this);
	}

}
