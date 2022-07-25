package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.backpack.EnderBackpackItem;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.common.PlayerSlot;
import dev.xkmc.l2backpack.content.worldchest.WorldChestItem;
import dev.xkmc.l2backpack.content.worldchest.WorldChestMenuPvd;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

@SerialClass
public class SlotClickToServer extends SerialPacketBase {

	/**
	 * slot click for backpack
	 */
	@SerialClass.SerialField
	private int index, slot, wid;

	@Deprecated
	public SlotClickToServer() {

	}

	public SlotClickToServer(int index, int slot, int wid) {
		this.index = index;
		this.slot = slot;
		this.wid = wid;
	}

	@Override
	public void handle(NetworkEvent.Context ctx) {
		ServerPlayer player = ctx.getSender();
		if (player == null) return;
		ItemStack stack;
		Container container = null;
		PlayerSlot playerSlot;
		if (wid == -1) {
			stack = CuriosCompat.getSlot(player, MiscEventHandler::canOpen);
			playerSlot = PlayerSlot.ofCurio(player);
		} else if (slot >= 0) {
			stack = player.getInventory().getItem(slot);
			playerSlot = PlayerSlot.ofInventory(slot);
		} else {
			AbstractContainerMenu menu = player.containerMenu;
			if (wid == 0 || menu.containerId == 0 || wid != menu.containerId) return;
			playerSlot = PlayerSlot.ofOtherInventory(slot, index, wid, menu);
			stack = menu.getSlot(index).getItem();
			container = menu.getSlot(index).container;
		}

		if (playerSlot != null && stack.getItem() instanceof BaseBagItem bag) {
			bag.open(player, playerSlot, stack);
		} else if (stack.getItem() instanceof EnderBackpackItem) {
			NetworkHooks.openScreen(player, new SimpleMenuProvider((id, inv, pl) ->
					ChestMenu.threeRows(id, inv, pl.getEnderChestInventory()), stack.getDisplayName()));
		} else if (stack.getItem() instanceof WorldChestItem chest) {
			new WorldChestMenuPvd(player, stack, chest).open();
			if (container != null) {
				container.setChanged();
			}
		}
	}
}
