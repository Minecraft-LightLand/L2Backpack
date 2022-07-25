package dev.xkmc.l2backpack.content.arrowbag;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

public final class ArrowBagMenuPvd implements MenuProvider {

	private final ServerPlayer player;
	private final int slot;
	private final ItemStack stack;

	public ArrowBagMenuPvd(ServerPlayer player, InteractionHand hand, ItemStack stack) {
		this.player = player;
		slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
		this.stack = stack;
	}

	public ArrowBagMenuPvd(ServerPlayer player, int slot, ItemStack stack) {
		this.player = player;
		this.slot = slot;
		this.stack = stack;
	}

	@Override
	public Component getDisplayName() {
		return stack.getDisplayName();
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		CompoundTag tag = stack.getOrCreateTag();
		UUID uuid = tag.getUUID("container_id");
		return new ArrowBagContainer(id, inventory, slot, uuid);
	}

	public void writeBuffer(FriendlyByteBuf buf) {
		CompoundTag tag = stack.getOrCreateTag();
		UUID id = tag.getUUID("container_id");
		buf.writeInt(slot);
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
