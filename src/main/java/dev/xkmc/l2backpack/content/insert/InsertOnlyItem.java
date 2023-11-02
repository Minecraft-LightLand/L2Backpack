package dev.xkmc.l2backpack.content.insert;

import dev.xkmc.l2backpack.init.data.BackpackConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

public interface InsertOnlyItem extends OverlayInsertItem {

	default boolean isValidContent(ItemStack carried) {
		return true;
	}

	@Override
	default boolean clientInsert(ItemStack storage, ItemStack carried, int cid, Slot slot, boolean perform, int button) {
		if (carried.isEmpty()) return false;
		if (!isValidContent(carried)) return false;
		if (!BackpackConfig.CLIENT.allowBackpackInsert(button)) return false;
		if (perform)
			sendInsertPacket(cid, carried, slot);
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	default boolean mayClientTake() {
		return false;
	}

	@Override
	default ItemStack takeItem(ItemStack storage, ServerPlayer player) {
		return ItemStack.EMPTY;
	}

	@Nullable
	default IItemHandler getInvCap(ItemStack storage, ServerPlayer player) {
		var opt = storage.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
		if (opt.isEmpty()) return null;
		return opt.get();
	}

	@Override
	default void attemptInsert(ItemStack storage, ItemStack carried, ServerPlayer player) {
		var handler = getInvCap(storage, player);
		if (handler == null) return;
		ItemStack remain = ItemHandlerHelper.insertItem(handler, carried.copy(), false);
		carried.setCount(remain.getCount());
	}

}
