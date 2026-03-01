package dev.xkmc.l2backpack.content.bag;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;

public class PotionBag extends AbstractBag {

	public PotionBag(Properties props) {
		super(props);
	}

	@Override
	public boolean isValidContent(ItemStack stack) {
		return !stack.isStackable() && stack.getItem() instanceof PotionItem;
	}

}
