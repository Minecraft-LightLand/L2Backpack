package dev.xkmc.l2backpack.network;

import dev.xkmc.l2backpack.content.insert.OverlayInsertItem;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class DrawerInteractToServer extends SerialPacketBase {

	public enum Type {
		INSERT, TAKE
	}

	@SerialClass.SerialField
	public Type type;

	@SerialClass.SerialField
	public int wid, slot;

	@SerialClass.SerialField
	public ItemStack stack;


	@Deprecated
	public DrawerInteractToServer() {

	}

	public DrawerInteractToServer(Type type, int wid, int slot, ItemStack carried) {
		this.type = type;
		this.wid = wid;
		this.slot = slot;
		this.stack = carried;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ServerPlayer player = context.getSender();
		if (player == null) return;
		AbstractContainerMenu menu = player.containerMenu;
		if (menu.containerId != wid) return;
		if (wid != 0 && !menu.getSlot(slot).allowModification(player)) return;
		ItemStack storage = wid == 0 ? player.getInventory().getItem(slot) : menu.getSlot(slot).getItem();
		if (!(storage.getItem() instanceof OverlayInsertItem drawerItem)) return;
		drawerItem.serverTrigger(storage, player);
		ItemStack carried = menu.getCarried();
		if (player.isCreative() && wid == 0) {
			carried = stack;
		}
		if (type == Type.TAKE) {
			ItemStack stack = drawerItem.takeItem(storage, player);
			if (player.isCreative() && wid == 0) {
				carried = stack;
			} else {
				menu.setCarried(stack);
			}
		} else {
			drawerItem.attemptInsert(storage, carried, player);
		}
		if (wid != 0) {
			menu.getSlot(slot).setChanged();
		}
		if (player.isCreative() && wid == 0) {
			L2Backpack.HANDLER.toClientPlayer(new CreativeSetCarryToClient(carried), player);
		}
	}

}
