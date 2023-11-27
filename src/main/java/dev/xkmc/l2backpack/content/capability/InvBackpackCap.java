package dev.xkmc.l2backpack.content.capability;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public abstract class InvBackpackCap<T extends IItemHandlerModifiable> implements PickupModeCap {

	@Nullable
	public abstract T getInv(PickupTrace trace);

	public boolean mayStack(T inv, int slot, ItemStack stack) {
		return ItemStack.isSameItemSameTags(inv.getStackInSlot(slot), stack);
	}

	@Override
	public int doPickup(ItemStack stack, PickupTrace trace) {
		if (stack.isEmpty() || getPickupMode().pickup() == PickupMode.NONE) return 0;
		if (!trace.push(getSignature(), getPickupMode())) return 0;
		int ans = doPickupInternal(stack, trace);
		trace.pop();
		return ans;
	}

	private int doPickupInternal(ItemStack stack, PickupTrace trace) {
		T inv = getInv(trace);
		if (inv == null) return 0;
		int ans = 0;
		if (trace.getMode().pickup() == PickupMode.NONE) return ans;
		for (int i = 0; i < inv.getSlots(); i++) {
			if (stack.isEmpty()) break;
			if (mayStack(inv, i, stack)) {
				ItemStack result = inv.insertItem(i, stack, false);
				int count = stack.getCount();
				int remain = result.getCount();
				ans += count - remain;
				stack.setCount(remain);
			} else {
				ItemStack slot = inv.getStackInSlot(i);
				if (slot.isEmpty()) continue;
				var opt = slot.getCapability(PickupModeCap.TOKEN).resolve();
				if (opt.isEmpty()) continue;
				ans += opt.get().doPickup(stack, trace);
			}
		}
		if (trace.getMode().pickup() == PickupMode.STACKING) return ans;
		for (int i = 0; i < inv.getSlots(); i++) {
			if (stack.isEmpty()) break;
			ItemStack result = inv.insertItem(i, stack.copy(), false);
			int count = stack.getCount();
			int remain = result.getCount();
			ans += count - remain;
			stack.setCount(remain);
		}
		return ans;
	}

}
