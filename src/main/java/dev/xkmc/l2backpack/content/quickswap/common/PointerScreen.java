package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.common.BaseBagMenu;
import dev.xkmc.l2backpack.content.common.BaseOpenableScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PointerScreen<T extends BaseBagMenu<T>> extends BaseOpenableScreen<T> {

	public PointerScreen(T cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
		var sm = menu.sprite.get();
		var sr = sm.getRenderer(this);
		sr.start(g);
		int i = SingleSwapItem.getSelected(menu.getStack());
		sr.draw(g, "grid", "sel", 18 * i - 2, -2);
	}

}
