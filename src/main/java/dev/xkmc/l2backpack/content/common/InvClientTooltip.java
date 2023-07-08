package dev.xkmc.l2backpack.content.common;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record InvClientTooltip(InvTooltip inv) implements ClientTooltipComponent {

	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/gui/container/bundle.png");

	@Override
	public int getHeight() {
		return BaseBagItem.getItems(inv.stack()).size() / 9 * 18 + 2;
	}

	@Override
	public int getWidth(Font font) {
		return 18 * 9;
	}

	@Override
	public void renderImage(Font font, int mx, int my, GuiGraphics g) {
		var list = BaseBagItem.getItems(inv.stack());
		for (int i = 0; i < list.size(); i++) {
			renderSlot(font, mx + i % 9 * 18, my + i / 9 * 18, g, list.get(i));
		}
	}

	private void renderSlot(Font font, int x, int y, GuiGraphics g, ItemStack stack) {
		this.blit(g, x, y);
		if (stack.isEmpty()) {
			return;
		}
		g.renderItem(stack, x + 1, y + 1, 0);
		g.renderItemDecorations(font, stack, x + 1, y + 1);
	}

	private void blit(GuiGraphics g, int x, int y) {
		g.blit(TEXTURE_LOCATION, x, y, 0, 0, 0, 18, 18, 128, 128);
	}


}
