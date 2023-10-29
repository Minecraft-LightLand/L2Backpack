package dev.xkmc.l2backpack.content.drawer;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public interface BaseDrawerInvAccess extends IItemHandlerModifiable {

	BaseDrawerItem drawerItem();

	ItemStack drawerStack();

	ServerPlayer player();

	int getStoredCount();

	boolean isEmpty();

	default Item getStoredItem() {
		return BaseDrawerItem.getItem(drawerStack());
	}

	default ItemStack getStoredStack() {
		if (isEmpty() || getStoredCount() == 0) return ItemStack.EMPTY;
		return new ItemStack(getStoredItem(), getStoredCount());
	}

	default void setStoredItem(Item item) {
		drawerItem().setItem(drawerStack(), item, player());
	}

	void setStoredCount(int count);

	default boolean isItemValid(ItemStack stack) {
		if (stack.isEmpty()) return true;
		if (stack.hasTag()) return false;
		if (isEmpty()) return true;
		return getStoredItem() == stack.getItem();
	}

	@Override
	default int getSlots() {
		return 1;
	}

	@Override
	default int getSlotLimit(int slot) {
		Item item = getStoredItem();
		return BaseDrawerItem.getStacking(drawerStack()) * item.getMaxStackSize();
	}

	default int getMax(ItemStack stack) {
		return BaseDrawerItem.getStacking(drawerStack()) * stack.getMaxStackSize();
	}

	@Override
	default boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return isItemValid(stack);
	}

	@Override
	default void setStackInSlot(int slot, @NotNull ItemStack stack) {
		setStoredItem(stack.getItem());
		setStoredCount(stack.getCount());
	}

	@Override
	default @NotNull ItemStack getStackInSlot(int slot) {
		return getStoredStack();
	}

	@Override
	default @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) return stack;
		if (!isItemValid(stack)) return stack;
		boolean empty = isEmpty();
		int current = empty ? 0 : getStoredCount();
		int input = stack.getCount();
		int allow = Math.min(getMax(stack) - current, input);
		if (!simulate) {
			if (empty)
				setStoredItem(stack.getItem());
			setStoredCount(current + allow);
		}
		if (input == allow) {
			return ItemStack.EMPTY;
		}
		return ItemHandlerHelper.copyStackWithSize(stack, input - allow);
	}

	@Override
	default @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		return drawerItem().takeItem(drawerStack(), amount, player(), simulate);
	}

	default boolean mayStack(BaseDrawerInvAccess inv, int slot, ItemStack stack) {
		return !isEmpty() && isItemValid(stack);
	}

}
