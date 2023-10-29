package dev.xkmc.l2backpack.content.remote.drawer;

import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.drawer.IDrawerHandler;
import dev.xkmc.l2backpack.content.remote.common.DrawerAccess;
import dev.xkmc.l2backpack.init.advancement.BackpackTriggers;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record EnderDrawerItemHandler(DrawerAccess access, boolean logistics) implements IDrawerHandler {

	@Override
	public int getSlots() {
		return 2;
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		if (slot == 1) {
			return ItemStack.EMPTY;
		}
		return new ItemStack(access.item(), access.getCount());
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (stack.hasTag() || stack.getItem() != access.item()) return stack;
		int count = access.getCount();
		int max = access.item().getMaxStackSize() * BaseDrawerItem.getStacking();
		int insert = Math.min(max - count, stack.getCount());
		if (!simulate) {
			access.setCount(count + insert);
			if (logistics && insert > 0) {
				access.getOwner().ifPresent(BackpackTriggers.REMOTE::trigger);
			}
		}
		return insert == stack.getCount() ? ItemStack.EMPTY : new ItemStack(access.item(), stack.getCount() - insert);
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		int count = access.getCount();
		int take = Math.min(count, amount);
		if (!simulate) {
			access.setCount(count - take);
			if (logistics && take > 0) {
				access.getOwner().ifPresent(BackpackTriggers.REMOTE::trigger);
			}
		}
		return new ItemStack(access.item(), take);
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64 * access.item().getMaxStackSize();
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return !stack.hasTag() && stack.getItem() == access.item();
	}
}
