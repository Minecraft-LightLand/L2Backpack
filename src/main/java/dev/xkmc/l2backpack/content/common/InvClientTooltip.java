package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.content.client.DrawerCountDeco;
import dev.xkmc.l2core.util.Proxy;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record InvClientTooltip(InvTooltip inv) implements ClientTooltipComponent {

	@Override
	public int getHeight() {
		return inv.h() * 18 + 2;
	}

	@Override
	public int getWidth(Font font) {
		return 18 * inv.w();
	}

	@Override
	public void renderImage(Font font, int mx, int my, GuiGraphics g) {
		var list = inv.item().getInvItems(inv.stack(), Proxy.getClientPlayer());
		for (int i = 0; i < Math.min(inv.w() * inv.h(), list.size()); i++) {
			renderSlot(font, mx + i % inv.w() * 18,
					my + i / inv.w() * 18, g, list.get(i));
		}
	}

	private void renderSlot(Font font, int x, int y, GuiGraphics g, ItemStack stack) {
		this.blit(g, x, y);
		if (stack.isEmpty()) {
			return;
		}
		DrawerCountDeco.startTooltipRendering();
		g.renderItem(stack, x + 1, y + 1, 0);
		g.renderItemDecorations(font, stack, x + 1, y + 1);
		DrawerCountDeco.stopTooltipRendering();
	}

	private void blit(GuiGraphics g, int x, int y) {
		g.blitSprite(ResourceLocation.withDefaultNamespace("container/bundle/slot"), x, y, 18, 20);
	}


}
