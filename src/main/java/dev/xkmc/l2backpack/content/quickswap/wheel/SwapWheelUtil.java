package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.common.WheelSelectToServer;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class SwapWheelUtil {

	private static final int PREV_X = -64;
	private static final int NEXT_X = 48;
	private static final int SIDE_Y = -24;

	public static void renderSideItems(GuiGraphics g, ItemStack prev, ItemStack next) {
		int x0 = g.guiWidth() / 2;
		int y0 = g.guiHeight() / 2;
		float s = Math.min(x0, y0) / 2.0F * 0.03F;
		float sideScale = s * 1.5f;
		Font font = Minecraft.getInstance().font;
		int textY = y0 + 48 + (int) ((SIDE_Y + 20) * sideScale);
		if (!prev.isEmpty()) {
			g.pose().pushPose();
			g.pose().translate(x0, y0 + 48, 0.0F);
			g.pose().scale(sideScale, sideScale, sideScale);
			g.renderItem(prev, PREV_X, SIDE_Y);
			g.pose().popPose();
			Component name = prev.getHoverName();
			int tx = x0 + (int) ((PREV_X + 8) * sideScale);
			g.drawString(font, name, tx - font.width(name) / 2, textY, 0xffffff);
		}
		if (!next.isEmpty()) {
			g.pose().pushPose();
			g.pose().translate(x0, y0 + 48, 0.0F);
			g.pose().scale(sideScale, sideScale, sideScale);
			g.renderItem(next, NEXT_X, SIDE_Y);
			g.pose().popPose();
			Component name = next.getHoverName();
			int tx = x0 + (int) ((NEXT_X + 8) * sideScale);
			g.drawString(font, name, tx - font.width(name) / 2, textY, 0xffffff);
		}
	}

	public static void select(int i, int wheelIndex) {
		L2Backpack.HANDLER.toServer(new WheelSelectToServer(i, wheelIndex, L2Keys.hasShiftDown()));
	}

	public static void shortPress(int selected, int wheelIndex) {
		L2Backpack.HANDLER.toServer(new WheelSelectToServer(selected, wheelIndex, L2Keys.hasShiftDown()));
	}

}
