package dev.xkmc.l2backpack.content.bag;

import net.minecraft.world.item.ItemStack;

public class EquipmentBag extends AbstractBag {

	public EquipmentBag(Properties props) {
		super(props);
	}

	public int getMaxFactor() {
		return 4;
	}

	@Override
	public boolean isValidContent(ItemStack stack) {
		return stack.isDamageableItem();
	}

}
