package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.entry.SetSwapEntry;
import dev.xkmc.l2backpack.content.quickswap.entry.SetSwapHandler;
import dev.xkmc.l2backpack.content.quickswap.type.ISetSwapAction;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import dev.xkmc.l2backpack.content.quickswap.wheel.SetSwapWheel;
import dev.xkmc.l2itemselector.overlay.WheelAdaptor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record SetSwapToken(ISetSwapItem item, ItemStack stack, QuickSwapType type)
		implements IQuickSwapToken<SetSwapEntry> {

	public void setSelected(int slot) {
		SingleSwapItem.setSelected(stack, slot);
	}

	public List<SetSwapEntry> getList() {
		return SetSwapEntry.parse(this, BaseBagItem.getItems(stack), item.getRows());
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
		if (!(type instanceof ISetSwapAction action)) return;
		List<ItemStack> list = getRawList();
		int row = list.size() / item.getRows();
		int ind = getSelected();
		action.swapSet(player, new SetSwapHandler(list, this::isLocked, i -> i * row + ind));
		BaseBagItem.setItems(stack, list);
	}

	@Override
	public boolean isLocked(int i) {
		return item.getToggle(stack, null).isLocked(i);
	}

	@Override
	public Optional<WheelAdaptor> get(@Nullable Player player, int wheelIndex, ItemStack prev, ItemStack next) {
		return Optional.of(new SetSwapWheel(this, wheelIndex, prev, next));
	}

}
