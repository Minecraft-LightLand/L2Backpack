package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.type.MultiOverlayToken;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record SetSwapToken(ISetSwapItem item, ItemStack stack, QuickSwapType type)
		implements IQuickSwapToken<MultiOverlayToken> {

	public void setSelected(int slot) {
		SingleSwapItem.setSelected(stack, slot);
	}

	public List<MultiOverlayToken> getList() {
		return MultiOverlayToken.parse(this, BaseBagItem.getItems(stack), item.getRows());
	}

	private List<ItemStack> getRawList() {
		return BaseBagItem.getItems(stack);
	}

	public int getSelected() {
		return SingleSwapItem.getSelected(stack);
	}

	public void shrink(int i) {
		throw new UnsupportedOperationException("set swap does not support shrink");
	}

	@Override
	public void swap(Player player) {
		if (!type.canSwap()) return;
		List<ItemStack> list = getRawList();
		int row = list.size() / item.getRows();
		int ind = getSelected();
		for (int i = 0; i < item.getRows(); i++) {
			int index = i * row + ind;
			ItemStack a = list.get(i);
			type.swap(player, a, r -> list.set(index, r));
		}
		BaseBagItem.setItems(stack, list);
	}

}
