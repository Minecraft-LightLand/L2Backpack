package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2itemselector.wheel.WheelAdaptor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public record ArmorWheelEntry(ItemStack stack) implements WheelAdaptor.Entry {

	public static EquipmentSlot getSlot(int i) {
		return EquipmentSlot.values()[5 - i];
	}

	public void render(GuiGraphics g, float x0, float y0, float ai, float r0, float r, float da, boolean sel) {
		float s = (sel ? 1.1f : 1) * Math.min(r * 0.015F, da * r0 / 16.0F);
		float dx = x0 + Mth.cos(ai) * r0;
		float dy = y0 + Mth.sin(ai) * r0;
		g.pose().pushPose();
		g.pose().translate(dx, dy, 0.0F);
		g.pose().scale(s, s, s);
		g.renderItem(this.stack, -8, -8);
		g.pose().popPose();
	}

}
