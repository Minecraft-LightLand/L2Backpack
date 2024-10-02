package dev.xkmc.l2backpack.content.quickswap.set;

import dev.xkmc.l2core.base.menu.data.BoolArrayDataSlot;

public record ClientSetToggle(BoolArrayDataSlot slot) implements ISetToggle {

	@Override
	public void toggle(int index) {

	}

	@Override
	public boolean isLocked(int index) {
		return slot.get(index);
	}

}
