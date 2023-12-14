package dev.xkmc.l2backpack.content.quickswap.entry;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record SetSwapHandler(List<ItemStack> list, Int2IntFunction mapping) implements ISetSwapHandler {

	@Override
	public ItemStack getStack(int index) {
		return list.get(mapping.get(index));
	}

	@Override
	public void replace(int index, ItemStack stack) {
		list.set(mapping.get(index), stack);
	}

}
