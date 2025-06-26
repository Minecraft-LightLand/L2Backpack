package dev.xkmc.l2backpack.content.quickswap.handswap;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public record MatcherData(Item[] matcher) {

	public static final MatcherData EMPTY;

	static {
		Item[] ans = new Item[9];
		for (int i = 0; i < 9; i++) ans[i] = Items.AIR;
		EMPTY = new MatcherData(ans);
	}

	public Item[] copy() {
		return matcher.clone();
	}

}
