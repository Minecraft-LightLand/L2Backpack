package dev.xkmc.l2backpack.content.quickswap.set;

import dev.xkmc.l2core.base.menu.data.BoolArrayDataSlot;
import dev.xkmc.l2core.init.reg.simple.DCVal;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record LongSetToggle(
		ItemStack stack, DCVal<Long> data, @Nullable BoolArrayDataSlot dataSlot
) implements ISetToggle {

	@Override
	public void toggle(int index) {
		data.set(stack, data.getOrDefault(stack, 0L) ^ (1L << index));
		if (dataSlot != null) dataSlot.set(isLocked(index), index);
	}

	@Override
	public boolean isLocked(int index) {
		return (data.getOrDefault(stack, 0L) & (1L << index)) != 0;
	}
}
