package dev.xkmc.l2backpack.content.quickswap.type;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface OverlayToken<T extends OverlayToken<T>> {

	IQuickSwapToken<T> token();

	List<ItemStack> asList();

	ItemStack getStack();

}
