package dev.xkmc.l2backpack.content.quickswap.set;

import dev.xkmc.l2backpack.content.common.BagSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class GenericSetBagSlot extends BagSlot {

	protected final ISetToggle toggle;
	protected final int index;

	public GenericSetBagSlot(IItemHandlerModifiable handler, ISetToggle toggle, int index, int x, int y) {
		super(handler, index, x, y);
		this.toggle = toggle;
		this.index = index;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return !isDisabled() && super.mayPlace(stack);
	}

	public void toggle() {
		toggle.toggle(index);
	}

	public boolean isDisabled() {
		return toggle.isLocked(index);
	}

}
