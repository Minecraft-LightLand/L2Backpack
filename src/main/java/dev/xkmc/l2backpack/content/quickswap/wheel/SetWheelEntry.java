package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.entry.SetSwapEntry;
import dev.xkmc.l2itemselector.wheel.WheelAdaptor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public record SetWheelEntry(SetSwapEntry set) implements WheelAdaptor.Entry {

	public void render(GuiGraphics g, float x0, float y0, float ai, float r0, float r, float da, boolean sel) {
		float s = (sel ? 1.1f : 1) * Math.min(r * 0.015F, da * r0 / 44.0F);
		float dx = x0 + Mth.cos(ai) * r0;
		float dy = y0 + Mth.sin(ai) * r0;
		g.pose().pushPose();
		g.pose().translate(dx, dy, 0.0F);
		g.pose().scale(s, s, s);
		for (int i = 0; i < 4; i++) {
			if (set.list().size() <= i) continue;
			var stack = set.list().get(i);
			if (stack.isEmpty()) continue;
			g.renderItem(stack, i % 2 == 0 ? -16 : 0, i <= 1 ? -16 : 0);
		}
		g.pose().popPose();
	}

}
