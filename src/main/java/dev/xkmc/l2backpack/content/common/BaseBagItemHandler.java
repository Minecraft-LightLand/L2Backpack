package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.content.remote.common.LBSavedData;
import dev.xkmc.l2backpack.content.restore.DimensionSourceData;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ComponentItemHandler;

public class BaseBagItemHandler extends ComponentItemHandler {

	private final BaseBagItem bag;
	private final ItemStack stack;

	public BaseBagItemHandler(ItemStack parent) {
		super(parent, LBItems.BACKPACK_CONTENT.get(), 0);
		this.bag = (BaseBagItem) parent.getItem();
		this.stack = parent;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (stack.isEmpty() || slot < 0 || slot >= getSlots()) return ItemStack.EMPTY;
		return super.getStackInSlot(slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (stack.isEmpty() || slot < 0 || slot >= getSlots()) return;
		super.setStackInSlot(slot, stack);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack toInsert, boolean simulate) {
		if (stack.isEmpty() || slot < 0 || slot >= getSlots()) return toInsert;
		return super.insertItem(slot, toInsert, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (stack.isEmpty() || slot < 0 || slot >= getSlots()) return ItemStack.EMPTY;
		return super.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlots() {
		return bag.getRows(stack) * 9;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return stack.isEmpty() || bag.isItemValid(slot, stack);
	}

	@Override
	protected void onContentsChanged(int slot, ItemStack oldStack, ItemStack newStack) {
		super.onContentsChanged(slot, oldStack, newStack);
		saveCallback();
	}

	private CallbackData callbackData = null;

	public void attachEnv(ServerPlayer player, PlayerSlot<?> hand) {
		if (hand.data() instanceof DimensionSourceData data) {
			callbackData = new CallbackData(this, player, data);
		}
	}

	private void saveCallback() {
		if (callbackData == null) return;
		callbackData.setChanged();
	}

	private record CallbackData(BaseBagItemHandler parent, ServerPlayer player, DimensionSourceData data) {

		private void setChanged() {
			var opt = LBSavedData.get(player.serverLevel())
					.getStorageWithoutPassword(data.uuid(), data.color());
			if (opt.isEmpty()) return;
			var cont = opt.get();
			var slot = cont.get().getItem(data.slot());
			if (parent.stack != slot) {
				parent.callbackData = null;
				return;
			}
			cont.get().setChanged();
		}

	}


}
