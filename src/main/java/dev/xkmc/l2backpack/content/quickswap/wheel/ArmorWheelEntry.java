package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.common.SingleSwapItem;
import dev.xkmc.l2backpack.content.quickswap.type.ArmorSwapType;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2itemselector.wheel.WheelAdaptor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
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
		if (!sel) return;
		Player player = Minecraft.getInstance().player;
		if (player == null) return;
		var type = QuickSwapTypes.ARMOR;
		EquipmentSlot target = SingleSwapItem.getEquipmentSlotForItem(stack);
		for (int i = 0; i < 4; i++) {
			int x = (int) x0 + (i % 2 == 0 ? -17 : 1), y = (int) y0 + (i <= 1 ? -17 : 1);
			EquipmentSlot slot = getSlot(i);
			ItemStack stack = player.getItemBySlot(slot);
			ItemStack targetStack = player.getItemBySlot(target);
			ArmorSwapType.renderArmorSlot(g, x, y, 64, target == slot, !type.maySwapOut(targetStack));
			g.renderItem(stack, x, y);
		}
	}
}
