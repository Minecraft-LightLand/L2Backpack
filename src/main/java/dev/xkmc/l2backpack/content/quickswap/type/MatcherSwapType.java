package dev.xkmc.l2backpack.content.quickswap.type;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class MatcherSwapType extends QuickSwapType {

	private final boolean allowsOffhand;

	MatcherSwapType(String name, int index, boolean allowsOffhand) {
		super(name, index);
		this.allowsOffhand = allowsOffhand;
	}

	public MatcherSwapType(String name, boolean allowsOffhand) {
		super(name);
		this.allowsOffhand = allowsOffhand;
	}

	@Override
	public ItemStack getSignatureItem(Player player) {
		ItemStack main = player.getMainHandItem();
		if (match(main)) return main;
		if (allowsOffhand) {
			ItemStack off = player.getOffhandItem();
			if (match(off)) return off;
		}
		return ItemStack.EMPTY;
	}

	public abstract boolean match(ItemStack stack);

	public boolean allowsOffhand() {
		return allowsOffhand;
	}

}
