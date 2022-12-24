package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.common.PlayerSlot;
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

public final class SimpleMenuPvd implements MenuProvider {

	public interface BagMenuFactory {

		AbstractContainerMenu create(int id, Inventory inventory, PlayerSlot slot, UUID uuid, Component title);

	}

	private final ServerPlayer player;
	private final PlayerSlot slot;
	private final ItemStack stack;
	private final BagMenuFactory factory;

	public SimpleMenuPvd(ServerPlayer player, PlayerSlot slot, ItemStack stack, BagMenuFactory factory) {
		this.player = player;
		this.slot = slot;
		this.stack = stack;
		this.factory = factory;
	}

	@Override
	public Component getDisplayName() {
		return stack.getHoverName();
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		CompoundTag tag = stack.getOrCreateTag();
		UUID uuid = tag.getUUID("container_id");
		return factory.create(id, inventory, slot, uuid, getDisplayName());
	}

	public void writeBuffer(FriendlyByteBuf buf) {
		CompoundTag tag = stack.getOrCreateTag();
		UUID id = tag.getUUID("container_id");
		slot.write(buf);
		buf.writeUUID(id);
	}

	public void open() {
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.getBoolean("init")) {
			tag.putBoolean("init", true);
			tag.putUUID("container_id", UUID.randomUUID());
		}
		NetworkHooks.openScreen(player, this, this::writeBuffer);
	}

}
