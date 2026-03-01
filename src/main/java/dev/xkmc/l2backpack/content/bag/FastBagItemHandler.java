package dev.xkmc.l2backpack.content.bag;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public record FastBagItemHandler(AbstractBag bag, ItemStack bagStack,
								 NonNullList<ItemStack> list)
		implements IItemHandlerModifiable {

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		list.set(slot, stack);
		bag.setContent(bagStack, list);
	}

	@Override
	public int getSlots() {
		return bag.getInvSize(bagStack);
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		return list.get(slot);
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (list.get(slot).isEmpty()) {
			if (!simulate) {
				list.set(slot, stack);
				bag.setContent(bagStack, list);
			}
			return ItemStack.EMPTY;
		}
		return stack;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) return ItemStack.EMPTY;
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

}
