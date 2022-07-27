package dev.xkmc.l2backpack.content.common;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public class ContentTransfer {

	public static void transfer(List<ItemStack> list, IItemHandler cap) {
		int n = list.size();
		for (int i = 0; i < n; i++) {
			ItemStack stack = list.get(i);
			stack = ItemHandlerHelper.insertItem(cap, stack, false);
			list.set(i, stack);
		}
	}

}
