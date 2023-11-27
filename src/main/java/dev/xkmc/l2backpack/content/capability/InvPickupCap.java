package dev.xkmc.l2backpack.content.capability;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public abstract class InvPickupCap<T extends IItemHandlerModifiable> implements PickupModeCap {

	@Nullable
	public abstract T getInv(PickupTrace trace);

	public boolean mayStack(T inv, int slot, ItemStack stack, PickupConfig config) {
		ItemStack old = inv.getStackInSlot(slot);
		if (old.getCapability(PickupModeCap.TOKEN).resolve().isPresent()) return false;
		if (config.pickup() == PickupMode.ALL && old.isEmpty()) {
			return true;
		}
		if (config.destroy() == DestroyMode.MATCH) {
			return stack.getItem() == old.getItem();
		}
		return ItemStack.isSameItemSameTags(old, stack);
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
		PickupMode pickup = trace.getMode().pickup();
		DestroyMode destroy = trace.getMode().destroy();
		if (pickup == PickupMode.NONE) return ans;
		boolean doClear = false;
		if (destroy == DestroyMode.ALL) {
			doClear = true;
		} else {
			for (int i = 0; i < inv.getSlots(); i++) {
				if (stack.isEmpty()) break;
				if (mayStack(inv, i, stack, trace.getMode())) {
					boolean empty = inv.getStackInSlot(i).isEmpty();
					if ((!doClear && empty) || destroy.attemptInsert) {
						int count = stack.getCount();
						ItemStack result = inv.insertItem(i, stack.copy(), false);
						int remain = result.getCount();
						stack.setCount(remain);
						ans += count - remain;
					}
					if (!empty && destroy != DestroyMode.NONE) {
						doClear = true;
					}
				} else {
					ItemStack slot = inv.getStackInSlot(i);
					if (slot.isEmpty()) continue;
					var opt = slot.getCapability(PickupModeCap.TOKEN).resolve();
					if (opt.isEmpty()) continue;
					ans += opt.get().doPickup(stack, trace);
				}
			}
		}
		if (pickup == PickupMode.ALL && destroy == DestroyMode.EXCESS)
			doClear = true;
		if (doClear) {
			ans += stack.getCount();
			stack.setCount(0);
		}
		return ans;
	}

}
