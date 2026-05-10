package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.SingleSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.WheelSelectToServer;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.overlay.ItemWheelEntry;
import dev.xkmc.l2itemselector.overlay.TextBox;
import dev.xkmc.l2itemselector.overlay.WheelAdaptor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record SingleSwapWheel(SingleSwapToken token, int wheelIndex,
                              ItemStack next) implements WheelAdaptor, IQuickSwapToken.SwapWheel {

	public List<Entry> getWheelContent() {
		List<ItemStack> src = token.getRawList();
		ArrayList<Entry> ans = new ArrayList<>();
		for (ItemStack e : src) {
			ans.add(new ItemWheelEntry(e));
		}

		return ans;
	}

	public int getIndex(Player player) {
		return token.getSelected();
	}

	public void render(GuiGraphics g, Player player) {
		WheelAdaptor.super.render(g, player);
		int index = this.getMouseSelect(player);
		ItemStack stack = ItemStack.EMPTY;
		if (index >= 0) {
			stack = token.getRawList().get(index);
		}
		boolean tooltip = !stack.isEmpty();
		if (stack.isEmpty()) stack = token.stack();
		int x0 = g.guiWidth() / 2;
		int y0 = g.guiHeight() / 2;
		float r = (float) Math.min(x0, y0) / 2.0F;
		float s = r * 0.03F;
		g.pose().pushPose();
		g.pose().translate((float) x0, (float) y0, 0.0F);
		g.pose().scale(s, s, s);
		g.renderItem(stack, -8, tooltip ? -16 : -8);
		if (!next.isEmpty()) {
			g.pose().translate(12, -4, 0);
			g.pose().scale(0.5f, 0.5f, 0.5f);
			g.renderItem(next, -8, -8);
		}
		g.pose().popPose();
		if (tooltip) {
			Component text = stack.getHoverName();
			Font font = Minecraft.getInstance().font;
			g.renderTooltip(font, stack.getHoverName(), 0, 0);
			TextBox box = new TextBox(g, 1, 0, x0, (int) ((float) y0 + s * 3.0F), (int) r);
			box.renderLongText(font, List.of(text));
		}

	}

	@Override
	public void select(int i) {
		L2Backpack.HANDLER.toServer(new WheelSelectToServer(i, wheelIndex, L2Keys.hasShiftDown()));
	}

}
