package dev.xkmc.l2backpack.content.drawer;

import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

@SerialClass
public class DrawerHandler implements IItemHandler {

	@SerialClass.SerialField(toClient = true)
	public Item item;

	@SerialClass.SerialField(toClient = true)
	public int count;

	@SerialClass.SerialField(toClient = true)
	public CompoundTag config;

	private final DrawerBlockEntity parent;

	public DrawerHandler(DrawerBlockEntity parent) {
		this.parent = parent;
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		return new ItemStack(item, count);
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		if (stack.hasTag()) {
			return stack;
		}
		int max = BaseDrawerItem.getStackingFactor(config) * stack.getMaxStackSize();
		if (count >= max) {
			return ItemStack.EMPTY;
		}
		if (item == Items.AIR) {
			int toInsert = Math.min(max, stack.getCount());
			if (!simulate) {
				item = stack.getItem();
				count = toInsert;
				parent.sync();
			}
			if (toInsert == stack.getCount()) {
				return ItemStack.EMPTY;
			}
			ItemStack ans = stack.copy();
			ans.setCount(stack.getCount() - toInsert);
			return ans;
		}
		if (item != stack.getItem()) {
			return stack;
		}
		int toInsert = Math.min(max - count, stack.getCount());
		if (!simulate) {
			item = stack.getItem();
			count += toInsert;
			parent.sync();
		}
		if (toInsert == stack.getCount()) {
			return ItemStack.EMPTY;
		}
		ItemStack ans = stack.copy();
		ans.setCount(stack.getCount() - toInsert);
		return ans;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (item == Items.AIR || count == 0) {
			return ItemStack.EMPTY;
		}
		int toExtract = Math.min(amount, count);
		ItemStack ans = new ItemStack(item, toExtract);
		if (!simulate) {
			count -= toExtract;
			if (count == 0) {
				item = Items.AIR;
			}
			parent.sync();
		}
		return ans;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64 * DrawerItem.MAX_STACK_SIZE;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return stack.getTag() == null && (item == Items.AIR || stack.getItem() == item);
	}
}
