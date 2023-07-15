package dev.xkmc.l2backpack.content.quickswap.merged;

import dev.xkmc.l2backpack.content.common.BaseOpenableScreen;
import dev.xkmc.l2library.base.menu.base.BaseContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MultiSwitchScreen<T extends BaseContainerMenu<T>> extends BaseOpenableScreen<T> {

	public MultiSwitchScreen(T cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	protected void renderBg(GuiGraphics stack, float pt, int mx, int my) {
		var sm = menu.sprite.get();
		var sr = sm.getRenderer(this);
		sr.start(stack);
		int offset = getMenu().slots.size() / 9 - 3;
		for (int i = 0; i < 9; i++)
			if (getMenu().getSlot(offset * 9 + i).getItem().isEmpty())
				sr.draw(stack, "arrow", "altas_arrow", i * 18 - 1, -1);
		offset++;
		for (int i = 0; i < 9; i++)
			if (getMenu().getSlot(offset * 9 + i).getItem().isEmpty())
				sr.draw(stack, "tool", "altas_tool", i * 18 - 1, -1);
		offset++;
		for (int i = 0; i < 9; i++)
			if (getMenu().getSlot(offset * 9 + i).getItem().isEmpty())
				sr.draw(stack, "armor", "altas_armor", i * 18 - 1, -1);
	}
}
