package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.content.common.BaseOpenableScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class WorldChestScreen extends BaseOpenableScreen<WorldChestContainer> {

	public WorldChestScreen(WorldChestContainer cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	protected void renderBg(GuiGraphics stack, float pt, int mx, int my) {
		var sm = menu.sprite.get();
		var sr = sm.getRenderer(this);
		sr.start(stack);
	}

}
