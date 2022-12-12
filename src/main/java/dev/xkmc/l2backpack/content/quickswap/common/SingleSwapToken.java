package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.quiver.Quiver;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record SingleSwapToken(IQuickSwapItem item, ItemStack stack,
							  QuickSwapType type) implements IQuickSwapToken {

	public void setSelected(int slot) {
		Quiver.setSelected(stack, slot);
	}

	public List<ItemStack> getList() {
		return BaseBagItem.getItems(stack);
	}

	public int getSelected() {
		return Quiver.getSelected(stack);
	}

	public void shrink(int i) {
		List<ItemStack> list = getList();
		list.get(getSelected()).shrink(i);
		BaseBagItem.setItems(stack, list);
	}

}
