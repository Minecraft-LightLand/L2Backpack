package dev.xkmc.l2backpack.content.bag;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EquipmentBag extends AbstractBag {

	public EquipmentBag(Properties props) {
		super(props);
	}

	@Override
	public boolean matches(ItemStack self, ItemStack stack) {
		return stack.isDamageableItem();
	}


}
