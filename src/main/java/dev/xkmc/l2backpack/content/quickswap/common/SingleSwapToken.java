package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import dev.xkmc.l2backpack.content.quickswap.type.SingleOverlayToken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record SingleSwapToken(IQuickSwapItem item, ItemStack stack, QuickSwapType type)
		implements IQuickSwapToken<SingleOverlayToken> {

	public void setSelected(int slot) {
		SingleSwapItem.setSelected(stack, slot);
	}

	public List<SingleOverlayToken> getList() {
		return SingleOverlayToken.parse(BaseBagItem.getItems(stack));
	}

	private List<ItemStack> getRawList() {
		return BaseBagItem.getItems(stack);
	}

	public int getSelected() {
		return SingleSwapItem.getSelected(stack);
	}

	public void shrink(int i) {
		List<ItemStack> list = getRawList();
		list.get(getSelected()).shrink(i);
		BaseBagItem.setItems(stack, list);
	}

	@Override
	public void swap(Player player) {
		if (!type.canSwap()) return;
		List<ItemStack> list = getRawList();
		int i = getSelected();
		ItemStack a = list.get(i);
		type.swap(player, a, r -> list.set(i, r));
		BaseBagItem.setItems(stack, list);
	}

}
