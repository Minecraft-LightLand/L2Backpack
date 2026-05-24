package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.common.SingleSwapToken;
import dev.xkmc.l2itemselector.wheel.ItemWheelEntry;
import dev.xkmc.l2itemselector.wheel.WheelContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record SingleSwapWheel(
		SingleSwapToken token, int wheelIndex
) implements SwapWheel<ItemWheelEntry> {

	public List<ItemWheelEntry> getWheelContent() {
		List<ItemStack> src = token.getRawList();
		ArrayList<ItemWheelEntry> ans = new ArrayList<>();
		for (ItemStack e : src) {
			ans.add(new ItemWheelEntry(e));
		}
		return ans;
	}

	public int getIndex(Player player) {
		return token.getSelected();
	}

	@Override
	public ItemStack getItem(List<ItemWheelEntry> list, int index) {
		if (index < 0 || index >= list.size()) return ItemStack.EMPTY;
		return list.get(index).stack();
	}

}
