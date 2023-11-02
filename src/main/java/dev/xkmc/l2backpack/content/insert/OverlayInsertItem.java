package dev.xkmc.l2backpack.content.insert;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.network.DrawerInteractToServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface OverlayInsertItem {

	boolean clientInsert(ItemStack storage, ItemStack carried, int cid, Slot slot, boolean perform, int button);

	boolean mayClientTake();

	default void serverTrigger(ItemStack storage, ServerPlayer player) {

	}

	ItemStack takeItem(ItemStack storage, ServerPlayer player);

	void attemptInsert(ItemStack storage, ItemStack carried, ServerPlayer player);

	default void sendInsertPacket(int cid, ItemStack carried, Slot slot) {
		int index = cid == 0 ? slot.getSlotIndex() : slot.index;
		L2Backpack.HANDLER.toServer(new DrawerInteractToServer(DrawerInteractToServer.Type.INSERT,
				cid, index, carried));
	}


}
