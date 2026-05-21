package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.common.SingleSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.WheelSelectToServer;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.wheel.WheelContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ArmorSwapWheel(
		SingleSwapToken token, int wheelIndex
) implements SwapWheel<ArmorWheelEntry> {

	public List<ArmorWheelEntry> getWheelContent() {
		List<ItemStack> src = token.getRawList();
		ArrayList<ArmorWheelEntry> ans = new ArrayList<>();
		for (ItemStack e : src) {
			ans.add(new ArmorWheelEntry(e));
		}

		return ans;
	}

	public int getIndex(Player player) {
		return token.getSelected();
	}

	@Override
	public void renderImpl(GuiGraphics g, Player player, List<ArmorWheelEntry> list, WheelContext ctx) {
		SwapWheel.super.renderImpl(g, player, list, ctx);
		ItemStack stack = token.stack();
		int x0 = g.guiWidth() / 2;
		int y0 = g.guiHeight() / 2;
		float r = (float) Math.min(x0, y0) / 2.0F;
		float s = r * 0.03F;
		g.pose().pushPose();
		g.pose().translate((float) x0, (float) y0, 0.0F);
		g.pose().scale(s, s, s);
		var sel = getMouseSelect(player).sel();
		if (sel < 0) g.renderItem(stack, -8, -8);
		g.pose().popPose();

	}

	@Override
	public void select(int i) {
		L2Backpack.HANDLER.toServer(new WheelSelectToServer(i, wheelIndex, L2Keys.hasShiftDown()));
	}

}
