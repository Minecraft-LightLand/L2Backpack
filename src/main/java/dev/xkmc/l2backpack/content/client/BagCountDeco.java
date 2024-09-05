package dev.xkmc.l2backpack.content.client;

import dev.xkmc.l2backpack.content.bag.AbstractBag;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;

public class BagCountDeco implements IItemDecorator {

	@Override
	public boolean render(GuiGraphics g, Font font, ItemStack stack, int x, int y) {
		if (!(stack.getItem() instanceof AbstractBag item)) return false;
		int size = item.getSize(stack);
		if (size == 0) return false;
		String s = size + "";
		g.pose().pushPose();
		int height = getZ();
		int width = font.width(s);
		int x0 = Math.max(3, 17 - width);
		g.pose().translate(x + x0, y + 16, height);
		if (width > 14) {
			float sc = 14f / width;
			g.pose().scale(sc, sc, 1);
		}
		int col = 0xffffff7f;
		g.drawString(font, s, 0, -7, col);
		g.pose().popPose();
		return true;
	}

	private static int getZ() {
		return 250;
	}

}
