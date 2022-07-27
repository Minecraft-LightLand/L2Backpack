package dev.xkmc.l2backpack.network;

import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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


	@Deprecated
	public DrawerInteractToServer() {

	}

	public DrawerInteractToServer(Type type, int wid, int slot) {
		this.type = type;
		this.wid = wid;
		this.slot = slot;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		Player player = context.getSender();
		if (player == null) return;
		AbstractContainerMenu menu = player.containerMenu;
		ItemStack drawer = menu.containerId == 0 ? player.getInventory().getItem(slot) : menu.getSlot(slot).getItem();
		if (!(drawer.getItem() instanceof BaseDrawerItem drawerItem)) return;

		if (type == Type.TAKE) {
			ItemStack stack = drawerItem.takeItem(drawer);
			menu.setCarried(stack);
		} else if (type == Type.INSERT) {
			ItemStack carried = menu.getCarried();
			if (BaseDrawerItem.canAccept(drawer, carried) && carried.getItem() != Items.AIR && !carried.hasTag()) {
				drawerItem.insert(drawer, menu.getCarried());
			}
		} else if (type == Type.SET) {
			ItemStack carried = menu.getCarried();
			if (drawerItem.canSetNewItem(drawer) && carried.getItem() != Items.AIR && !carried.hasTag()) {
				BaseDrawerItem.setItem(drawer, menu.getCarried().getItem());
				drawerItem.insert(drawer, menu.getCarried());
			}
		}


	}

}
