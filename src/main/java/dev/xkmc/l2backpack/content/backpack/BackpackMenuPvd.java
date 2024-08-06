package dev.xkmc.l2backpack.content.backpack;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public final class BackpackMenuPvd implements MenuProvider {

	private final ServerPlayer player;
	private final PlayerSlot<?> slot;
	private final ItemStack stack;
	private final BaseBagItem bag;

	public BackpackMenuPvd(ServerPlayer player, PlayerSlot<?> slot, BaseBagItem item, ItemStack stack) {
		this.player = player;
		this.slot = slot;
		this.stack = stack;
		bag = item;
	}

	@Override
	public Component getDisplayName() {
		return stack.getHoverName();
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		UUID uuid = LBItems.DC_CONT_ID.getOrDefault(stack, Util.NIL_UUID);
		return new BackpackMenu(id, inventory, slot, uuid, bag.getRows(stack), getDisplayName());
	}

	public void writeBuffer(RegistryFriendlyByteBuf buf) {
		slot.write(buf);
		buf.writeUUID(LBItems.DC_CONT_ID.getOrDefault(stack, Util.NIL_UUID));
		buf.writeInt(bag.getRows(stack));
	}

	public void open() {
		bag.checkInit(stack);
		player.openMenu(this, this::writeBuffer);
	}

}
