package dev.xkmc.l2backpack.content.quickswap.type;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface OverlayToken<T extends OverlayToken<T>> {

	List<ItemStack> asList();

	ItemStack getStack();

}
