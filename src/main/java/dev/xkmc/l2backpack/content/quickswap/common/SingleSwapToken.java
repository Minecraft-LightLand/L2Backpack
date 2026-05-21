package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.entry.SingleSwapEntry;
import dev.xkmc.l2backpack.content.quickswap.entry.SingleSwapHandler;
import dev.xkmc.l2backpack.content.quickswap.type.ISingleSwapAction;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2backpack.content.quickswap.wheel.ArmorSwapWheel;
import dev.xkmc.l2backpack.content.quickswap.wheel.SingleSwapWheel;
import dev.xkmc.l2itemselector.wheel.WheelAdaptor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record SingleSwapToken(IQuickSwapItem item, ItemStack stack, QuickSwapType type)
		implements IQuickSwapToken<SingleSwapEntry> {

	public void setSelected(int slot) {
		SingleSwapItem.setSelected(stack, slot);
	}

	public List<SingleSwapEntry> getList() {
		return SingleSwapEntry.parse(this, getRawList());
	}

	public List<ItemStack> getRawList() {
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
		if (!(type instanceof ISingleSwapAction action)) return;
		List<ItemStack> list = getRawList();
		int i = getSelected();
		action.swapSingle(player, new SingleSwapHandler(list, i));
		BaseBagItem.setItems(stack, list);
	}

	@Override
	public boolean isLocked(int i) {
		return false;
	}

	@Override
	public Optional<WheelAdaptor<?>> get(@Nullable Player player, int wheelIndex) {
		if (type == QuickSwapTypes.ARMOR)
			return Optional.of(new ArmorSwapWheel(this, wheelIndex));
		return Optional.of(new SingleSwapWheel(this, wheelIndex));
	}


}
