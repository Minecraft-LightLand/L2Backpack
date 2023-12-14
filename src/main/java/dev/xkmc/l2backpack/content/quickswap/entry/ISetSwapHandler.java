package dev.xkmc.l2backpack.content.quickswap.entry;

import net.minecraft.world.item.ItemStack;

public interface ISetSwapHandler {

	ItemStack getStack(int i);

	void replace(int i, ItemStack stack);

}
