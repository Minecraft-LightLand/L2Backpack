package dev.xkmc.l2backpack.content.capability;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public abstract class InvBackpackCap implements BackpackCap {

	@Nullable
	public abstract IItemHandlerModifiable getInv(PickupTrace trace);

	@Override
	public int doPickup(ItemStack stack, PickupTrace trace) {
		if (stack.isEmpty() || getMode() == PickupMode.NONE) return 0;
		if (!trace.push(getSignature(), getMode())) return 0;
		int ans = doPickupInternal(stack, trace);
		trace.pop();
		return ans;
	}

	private int doPickupInternal(ItemStack stack, PickupTrace trace) {
		IItemHandlerModifiable inv = getInv(trace);
		if (inv == null) return 0;
		int ans = 0;
		if (trace.getMode() == PickupMode.NONE) return ans;
		for (int i = 0; i < inv.getSlots(); i++) {
			if (stack.isEmpty()) break;
			ItemStack slot = inv.getStackInSlot(i);
			if (slot.isEmpty()) continue;
			var opt = slot.getCapability(BackpackCap.TOKEN).resolve();
			if (opt.isEmpty()) continue;
			ans += opt.get().doPickup(stack, trace);
		}
		if (trace.getMode() == PickupMode.STACKING) return ans;
		for (int i = 0; i < inv.getSlots(); i++) {
			if (stack.isEmpty()) break;
			ItemStack result = inv.insertItem(i, stack, false);
			int count = stack.getCount();
			int remain = result.getCount();
			ans += count - remain;
			stack.setCount(remain);
		}
		return ans;
	}

}
