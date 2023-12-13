package dev.xkmc.l2backpack.content.quickswap.type;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record SingleOverlayToken(IQuickSwapToken<SingleOverlayToken> token, ItemStack stack)
		implements OverlayToken<SingleOverlayToken> {

	public static List<SingleOverlayToken> parse(IQuickSwapToken<SingleOverlayToken> token, List<ItemStack> list) {
		return list.stream().map(e -> new SingleOverlayToken(token, e)).toList();
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
