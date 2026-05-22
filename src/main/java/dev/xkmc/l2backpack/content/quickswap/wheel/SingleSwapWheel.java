package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.common.SingleSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.WheelSelectToServer;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapManager;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.wheel.ItemWheelEntry;
import dev.xkmc.l2itemselector.wheel.WheelContext;
import dev.xkmc.l2itemselector.wheel.WheelHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

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
	public void renderImpl(GuiGraphics g, Player player, List list, WheelContext ctx) {
		SwapWheel.super.renderImpl(g, player, list, ctx);
		SwapWheel.super.renderCenter(g, player, list, ctx);
	}

	@Override
	public void select(int i) {
		L2Backpack.HANDLER.toServer(new WheelSelectToServer(i, wheelIndex, L2Keys.hasShiftDown()));
		var list = token.getRawList();
		if (i >= 0 && i < list.size() && token.type() == QuickSwapTypes.TOOL
				&& list.get(i).getItem() instanceof ProjectileWeaponItem) {
			var wheelTokens = QuickSwapManager.getWheelTokens(Minecraft.getInstance().player, L2Keys.hasShiftDown());
			for (int j = 0; j < wheelTokens.size(); j++) {
				if (wheelTokens.get(j).type() == QuickSwapTypes.ARROW) {
					WheelHandler.wheelIndex = j;
					break;
				}
			}
		}
	}

}
