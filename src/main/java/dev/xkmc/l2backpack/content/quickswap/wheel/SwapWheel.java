package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2itemselector.wheel.WheelAdaptor;
import net.minecraft.client.gui.GuiGraphics;

public interface SwapWheel<T extends WheelAdaptor.Entry> extends WheelAdaptor<T> {

	IQuickSwapToken<?> token();

	@Override
	default void renderIcon(GuiGraphics g, int x0, int y0, boolean left, float sideWidth, boolean hover) {
		float cx = left ? sideWidth / 2.0F : (float) g.guiWidth() - sideWidth / 2.0F;
		float r = Math.min((float) x0 / 1.5F, (float) y0) / 1.5F;
		float s = r * (hover ? 0.02f : 0.015F);
		g.pose().pushPose();
		g.pose().translate(cx, (float) y0, 0.0F);
		g.pose().scale(s, s, s);
		g.renderItem(token().stack(), -8, -8);
		g.pose().popPose();
	}

}
