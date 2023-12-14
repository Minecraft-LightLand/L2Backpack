package dev.xkmc.l2backpack.content.quickswap.entry;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record SingleSwapHandler(List<ItemStack> list, int index) implements ISingleSwapHandler {

	@Override
	public ItemStack getStack() {
		return list.get(index);
	}

	@Override
	public void replace(ItemStack stack) {
		list.set(index, stack);
	}

}
