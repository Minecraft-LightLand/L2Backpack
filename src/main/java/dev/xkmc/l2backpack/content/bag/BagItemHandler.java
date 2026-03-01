package dev.xkmc.l2backpack.content.bag;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public record BagItemHandler(AbstractBag bag, ItemStack bagStack) implements IItemHandlerModifiable {

	public BagItemHandler(ItemStack stack) {
		this((AbstractBag) stack.getItem(), stack);
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		var list = bag.getContent(bagStack);
		list.set(slot, stack);
		bag.setContent(bagStack, list);
	}

	@Override
	public int getSlots() {
		return bag.getInvSize(bagStack);
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		return bag.getContent(bagStack).get(slot);
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		var list = bag.getContent(bagStack);
		if (list.get(slot).isEmpty()) {
			if (!simulate) {
				list.set(slot, stack.copyWithCount(1));
				bag.setContent(bagStack, list);
			}
			return stack.copyWithCount(stack.getCount() - 1);
		}
		return stack;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) return ItemStack.EMPTY;
		var list = bag.getContent(bagStack);
		var ans = list.get(slot);
		if (!ans.isEmpty() && !simulate) {
			list.set(slot, ItemStack.EMPTY);
			bag.setContent(bagStack, list);
		}
		return ans;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return bag.isValidContent(stack);
	}

	public FastBagItemHandler toFast() {
		return new FastBagItemHandler(bag, bagStack, bag.getContent(bagStack));
	}
}
