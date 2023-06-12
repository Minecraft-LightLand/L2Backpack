package dev.xkmc.l2backpack.content.backpack;

import dev.xkmc.l2backpack.init.data.BackpackConfig;
import dev.xkmc.l2screentracker.screen.source.PlayerSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

public final class BackpackMenuPvd implements MenuProvider {

	private final ServerPlayer player;
	private final PlayerSlot<?> slot;
	private final ItemStack stack;

	public BackpackMenuPvd(ServerPlayer player, PlayerSlot<?> slot, ItemStack stack) {
		this.player = player;
		this.slot = slot;
		this.stack = stack;
	}

	@Override
	public Component getDisplayName() {
		return stack.getHoverName();
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		CompoundTag tag = stack.getOrCreateTag();
		UUID uuid = tag.getUUID("container_id");
		return new BackpackContainer(id, inventory, slot, uuid, tag.getInt("rows"), getDisplayName());
	}

	public void writeBuffer(FriendlyByteBuf buf) {
		CompoundTag tag = stack.getOrCreateTag();
		UUID id = tag.getUUID("container_id");
		slot.write(buf);
		buf.writeUUID(id);
		buf.writeInt(tag.getInt("rows"));
	}

	public void open() {
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.getBoolean("init")) {
			tag.putBoolean("init", true);
			tag.putUUID("container_id", UUID.randomUUID());
			if (!tag.contains("rows"))
				tag.putInt("rows", BackpackConfig.COMMON.initialRows.get());
		}
		NetworkHooks.openScreen(player, this, this::writeBuffer);
	}

}
