package dev.xkmc.l2backpack.content.quickswap.handswap;

import dev.xkmc.l2backpack.content.common.BagSlot;
import dev.xkmc.l2backpack.content.common.BaseOpenableScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;

public class HandswapScreen extends BaseOpenableScreen<HandswapMenu> {

	public HandswapScreen(HandswapMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
		var sm = menu.sprite.get();
		var sr = sm.getRenderer(this);
		sr.start(g);
		int sel = menu.inventory.selected;
		sr.draw(g, "grid", "sel", 18 * sel - 2, -2);
		for (int i = 0; i < 9; i++) {
			var slot = menu.getSlot("grid", i, 0);
			if (slot.getItem().isEmpty() || menu.getMatcher(i) == Items.AIR) continue;
			if (slot.getItem().getItem() != menu.getMatcher(i)) {
				sr.draw(g, "grid", "extra", 18 * i - 1, 17);
			}
		}
	}

	@Override
	public void renderSlot(GuiGraphics g, Slot slot) {
		super.renderSlot(g, slot);
		if (slot instanceof BagSlot s) {
			int i = s.getSlotIndex();
			if (menu.getMatcher(i) == Items.AIR) return;
			int x = slot.x;
			int y = slot.y;
			if (slot.getItem().isEmpty()) {
				g.renderFakeItem(menu.getMatcher(i).getDefaultInstance(), x, y);
				g.fillGradient(RenderType.guiOverlay(), x, y, x + 16, y + 16, 0x7f8B8B8B, 0x7f8B8B8B, 0);
			} else if (slot.getItem().getItem() != menu.getMatcher(i)) {
				y += 18;
				g.renderFakeItem(menu.getMatcher(i).getDefaultInstance(), x, y);
				g.fillGradient(RenderType.guiOverlay(), x, y, x + 16, y + 16, 0x7fC6C6C6, 0x7fC6C6C6, 0);
			}
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int btn) {
		if (Screen.hasShiftDown()) {
			for (int i = 0; i < 9; i++) {
				var slot = menu.getSlot("grid", i, 0);
				if (menu.getMatcher(i) == Items.AIR) continue;
				if (slot.getItem().isEmpty()) {
					int sx = leftPos + slot.x - 1;
					int sy = topPos + slot.y - 1;
					if (sx <= x && x <= sx + 18 && sy <= y && y <= sy + 18) {
						click(i);
						return true;
					}
				} else if (slot.getItem().getItem() != menu.getMatcher(i)) {
					int sx = leftPos + slot.x - 1;
					int sy = topPos + slot.y + 17;
					if (sx <= x && x <= sx + 18 && sy <= y && y <= sy + 18) {
						click(i);
						return true;
					}
				}
			}
		}
		return super.mouseClicked(x, y, btn);
	}

}
