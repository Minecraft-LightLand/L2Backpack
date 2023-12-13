package dev.xkmc.l2backpack.content.quickswap.type;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record MultiOverlayToken(IQuickSwapToken<MultiOverlayToken> token, List<ItemStack> list)
		implements OverlayToken<MultiOverlayToken> {
	public static List<MultiOverlayToken> parse(IQuickSwapToken<MultiOverlayToken> token, List<ItemStack> items, int size) {
		int row = items.size() / size;
		List<MultiOverlayToken> ans = new ArrayList<>();
		ItemStack[] arr = new ItemStack[size];
		for (int j = 0; j < row; j++) {
			for (int i = 0; i < size; i++) {
				arr[i] = items.get(i * row + j);
			}
			ans.add(new MultiOverlayToken(token, List.of(arr)));
		}
		return ans;
	}

	@Override
	public List<ItemStack> asList() {
		return list;
	}

	@Override
	public ItemStack getStack() {
		throw new UnsupportedOperationException("not supposed to get single stack from set swap");
	}

}
