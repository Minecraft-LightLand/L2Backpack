package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.content.arrowbag.ArrowBag;
import dev.xkmc.l2backpack.content.arrowbag.ArrowBagMenuPvd;
import dev.xkmc.l2backpack.content.backpack.BackpackItem;
import dev.xkmc.l2backpack.content.backpack.BackpackMenuPvd;
import dev.xkmc.l2backpack.content.backpack.EnderBackpackItem;
import dev.xkmc.l2backpack.content.worldchest.WorldChestItem;
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
		if (slot >= 0) {
			stack = ctx.getSender().getInventory().getItem(slot);
		} else {
			AbstractContainerMenu menu = ctx.getSender().containerMenu;
			if (wid == 0 || menu.containerId == 0 || wid != menu.containerId) return;
			stack = menu.getSlot(index).getItem();
			container = menu.getSlot(index).container;
		}
		if (slot >= 0 && stack.getItem() instanceof BackpackItem) {
			new BackpackMenuPvd(player, slot, stack).open();
		} else if (slot >= 0 && stack.getItem() instanceof ArrowBag) {
			new ArrowBagMenuPvd(player, slot, stack).open();
		} else if (stack.getItem() instanceof EnderBackpackItem) {
			NetworkHooks.openScreen(player, new SimpleMenuProvider((id, inv, pl) ->
					ChestMenu.threeRows(id, inv, pl.getEnderChestInventory()), stack.getDisplayName()));
		} else if (stack.getItem() instanceof WorldChestItem chest) {
			new WorldChestItem.MenuPvd(player, stack, chest).open();
			if (container != null) {
				container.setChanged();
			}
		}
	}
}
