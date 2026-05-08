package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.entry.SingleSwapEntry;
import dev.xkmc.l2backpack.content.quickswap.entry.SingleSwapHandler;
import dev.xkmc.l2backpack.content.quickswap.type.ISingleSwapAction;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import dev.xkmc.l2backpack.events.BackpackSel;
import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.overlay.ItemWheelEntry;
import dev.xkmc.l2itemselector.overlay.WheelAdaptor;
import dev.xkmc.l2itemselector.select.SetSelectedToServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
	public Optional<WheelAdaptor> get(@Nullable Player player) {
		return Optional.of(new SingleWheel(this));
	}

	record SingleWheel(SingleSwapToken token) implements WheelAdaptor.ItemWheel, SwapWheel {

		public List<WheelAdaptor.Entry> getWheelContent() {
			List<ItemStack> src = token.getRawList();
			ArrayList<WheelAdaptor.Entry> ans = new ArrayList<>();
			for (ItemStack e : src) {
				ans.add(new ItemWheelEntry(e));
			}

			return ans;
		}

		public int getIndex(Player player) {
			return token.getSelected();
		}

		@Override
		public ItemStack getItem(int i) {
			return token.getRawList().get(i);
		}

		@Override
		public void select(int i) {
			L2ItemSelector.PACKET_HANDLER.toServer(new SetSelectedToServer(i,
					BackpackSel.INSTANCE.getID(), L2Keys.hasCtrlDown(), true, L2Keys.hasShiftDown()));
			L2ItemSelector.PACKET_HANDLER.toServer(new SetSelectedToServer(BackpackSel.SWAP,
					BackpackSel.INSTANCE.getID(), L2Keys.hasCtrlDown(), true, L2Keys.hasShiftDown()));
		}
	}


}
