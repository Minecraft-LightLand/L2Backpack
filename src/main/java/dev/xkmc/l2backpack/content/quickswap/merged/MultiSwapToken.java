package dev.xkmc.l2backpack.content.quickswap.merged;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapItem;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.type.ISwapAction;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import dev.xkmc.l2backpack.content.quickswap.entry.SingleSwapEntry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record MultiSwapToken(IQuickSwapItem item, ItemStack stack, QuickSwapType type)
		implements IQuickSwapToken<SingleSwapEntry> {

	public void setSelected(int slot) {
		MultiSwitch.setSelected(stack, type, slot);
	}

	public List<SingleSwapEntry> getList() {
		return SingleSwapEntry.parse(this, BaseBagItem.getItems(stack)
				.subList(type.getIndex() * 9, type.getIndex() * 9 + 9));
	}

	public int getSelected() {
		return MultiSwitch.getSelected(stack, type);
	}

	public void shrink(int i) {
		List<ItemStack> list = BaseBagItem.getItems(stack);
		List<ItemStack> sublist = list.subList(type.getIndex() * 9, type.getIndex() * 9 + 9);
		sublist.get(getSelected()).shrink(i);
		BaseBagItem.setItems(stack, list);
	}

	@Override
	public void swap(Player player) {
		if (!(type instanceof ISwapAction action)) return;
		List<ItemStack> list = BaseBagItem.getItems(stack);
		List<ItemStack> sublist = list.subList(type.getIndex() * 9, type.getIndex() * 9 + 9);
		int i = getSelected();
		ItemStack a = sublist.get(i);
		action.swap(player, a, r -> sublist.set(i, r));
		BaseBagItem.setItems(stack, list);
	}

}
