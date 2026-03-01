package dev.xkmc.l2backpack.compat.bags;

import dev.xkmc.l2backpack.content.bag.AbstractBag;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class CompatBag extends AbstractBag {

	private final Predicate<ItemStack> pred;

	public CompatBag(Properties props, Predicate<ItemStack> pred) {
		super(props);
		this.pred = pred;
	}

	@Override
	public boolean isValidContent(ItemStack carried) {
		return pred.test(carried);
	}
}
