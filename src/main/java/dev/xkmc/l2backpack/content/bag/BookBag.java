package dev.xkmc.l2backpack.content.bag;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BookBag extends AbstractBag {

	public BookBag(Properties props) {
		super(props);
	}

	@Override
	public boolean matches(ItemStack self, ItemStack stack) {
		return stack.getItem() == Items.ENCHANTED_BOOK;
	}


}
