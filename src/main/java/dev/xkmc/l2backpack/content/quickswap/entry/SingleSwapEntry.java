package dev.xkmc.l2backpack.content.quickswap.entry;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record SingleSwapEntry(IQuickSwapToken<SingleSwapEntry> token, ItemStack stack)
		implements ISwapEntry<SingleSwapEntry> {

	public static List<SingleSwapEntry> parse(IQuickSwapToken<SingleSwapEntry> token, List<ItemStack> list) {
		return list.stream().map(e -> new SingleSwapEntry(token, e)).toList();
	}

	@Override
	public List<ItemStack> asList() {
		return List.of(stack);
	}

	@Override
	public ItemStack getStack() {
		return stack;
	}

}
