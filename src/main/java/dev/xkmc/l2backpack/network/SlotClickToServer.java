package dev.xkmc.l2backpack.network;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.backpack.EnderBackpackItem;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.common.PlayerSlot;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestItem;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestMenuPvd;
import dev.xkmc.l2backpack.content.restore.ScreenTracker;
import dev.xkmc.l2backpack.events.ClientEventHandler;
import dev.xkmc.l2backpack.events.quickaccess.QuickAccessClickHandler;
import dev.xkmc.l2backpack.init.advancement.BackpackTriggers;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
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
		AbstractContainerMenu menu = player.containerMenu;
		if (wid == -1) {
			stack = player.getItemBySlot(EquipmentSlot.CHEST);
			playerSlot = PlayerSlot.ofInventory(36 + EquipmentSlot.CHEST.getIndex());
			if (!ClientEventHandler.canOpen(stack)) {
				stack = CuriosCompat.getSlot(player, ClientEventHandler::canOpen);
				playerSlot = PlayerSlot.ofCurio(player);
			}
		} else if (slot >= 0) {
			stack = player.getInventory().getItem(slot);
			playerSlot = PlayerSlot.ofInventory(slot);
		} else {
			if (wid == 0 || menu.containerId == 0 || wid != menu.containerId) return;
			playerSlot = PlayerSlot.ofOtherInventory(slot, index, wid, menu);
			stack = menu.getSlot(index).getItem();
			container = menu.getSlot(index).container;
		}
		if (QuickAccessClickHandler.isAllowed(stack)) {
			QuickAccessClickHandler.handle(player, stack);
			return;
		}
		boolean others = false;
		if (playerSlot != null && stack.getItem() instanceof BaseBagItem bag) {
			bag.open(player, playerSlot, stack);
			if (wid != -1 || slot != -1 || index != -1)
				ScreenTracker.onServerOpen(player, menu, playerSlot);
		} else if (stack.getItem() instanceof EnderBackpackItem) {
			NetworkHooks.openScreen(player, new SimpleMenuProvider((id, inv, pl) ->
					ChestMenu.threeRows(id, inv, pl.getEnderChestInventory()), stack.getHoverName()));
		} else if (stack.getItem() instanceof WorldChestItem chest) {
			others = WorldChestItem.getOwner(stack).map(e -> !e.equals(player.getUUID())).orElse(false);
			new WorldChestMenuPvd(player, stack, chest).open();
			if (playerSlot != null) {
				if (wid != -1 || slot != -1 || index != -1) {
					ScreenTracker.onServerOpen(player, menu, playerSlot);
				}
			}
		}
		if (container != null) {
			container.setChanged();
		}
		if (playerSlot != null) {
			BackpackTriggers.SLOT_CLICK.trigger(player, playerSlot.type(), wid == -1 && slot == -1 && index == -1);
		}
		if (others) {
			BackpackTriggers.SHARE.trigger(player);
		}
	}
}
