package dev.xkmc.l2backpack.content.quickswap.entry;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ISwapEntry<T extends ISwapEntry<T>> {

	IQuickSwapToken<T> token();

	List<ItemStack> asList();

	ItemStack getStack();

}
