package dev.xkmc.l2backpack.network.drawer;

import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerItem;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.advancement.BackpackTriggers;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class DrawerInteractToServer extends SerialPacketBase {

	public enum Type {
		SET, INSERT, TAKE
	}

	@SerialClass.SerialField
	public Type type;

	@SerialClass.SerialField
	public int wid, slot;

	@SerialClass.SerialField
	public Item item;

	@SerialClass.SerialField
	public int count;


	@Deprecated
	public DrawerInteractToServer() {

	}

	public DrawerInteractToServer(Type type, int wid, int slot, ItemStack carried) {
		this.type = type;
		this.wid = wid;
		this.slot = slot;
		this.item = carried.getItem();
		this.count = carried.getCount();
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ServerPlayer player = context.getSender();
		if (player == null) return;
		AbstractContainerMenu menu = player.containerMenu;
		if (menu.containerId != wid) return;
		if (wid != 0 && !menu.getSlot(slot).allowModification(player)) return;
		ItemStack drawer = wid == 0 ? player.getInventory().getItem(slot) : menu.getSlot(slot).getItem();
		if (!(drawer.getItem() instanceof BaseDrawerItem drawerItem)) return;
		if (drawerItem instanceof EnderDrawerItem && EnderDrawerItem.getOwner(drawer).map(e -> !e.equals(player.getUUID())).orElse(false)) {
			BackpackTriggers.SHARE.trigger(player);
		}
		ItemStack carried = menu.getCarried();
		if (player.isCreative() && wid == 0) {
			carried = new ItemStack(item, count);
		}
		if (type == Type.TAKE) {
			ItemStack stack = drawerItem.takeItem(drawer, player);
			if (player.isCreative() && wid == 0) {
				carried = stack;
			} else {
				menu.setCarried(stack);
			}
			if (!stack.isEmpty()) {
				BackpackTriggers.DRAWER.trigger(player, Type.TAKE);
			}
		} else if (type == Type.INSERT) {
			if (BaseDrawerItem.canAccept(drawer, carried) && !carried.isEmpty() && !carried.hasTag()) {
				drawerItem.insert(drawer, carried, player);
				BackpackTriggers.DRAWER.trigger(player, Type.INSERT);
			}
		} else if (type == Type.SET) {
			if (drawerItem.canSetNewItem(drawer) && !carried.isEmpty() && !carried.hasTag()) {
				drawerItem.setItem(drawer, carried.getItem(), player);
				drawerItem.insert(drawer, carried, player);
				BackpackTriggers.DRAWER.trigger(player, Type.INSERT);
			}
		}
		if (player.isCreative() && wid == 0) {
			L2Backpack.HANDLER.toClientPlayer(new CreativeSetCarryToClient(carried), player);
		}
	}

}
