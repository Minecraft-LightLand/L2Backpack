package dev.xkmc.l2backpack.content.remote.worldchest;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2backpack.content.common.BaseOpenableScreen;
import dev.xkmc.l2library.base.menu.SpriteManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class WorldChestScreen extends BaseOpenableScreen<WorldChestContainer> {

	public WorldChestScreen(WorldChestContainer cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	protected void renderBg(PoseStack stack, float pt, int mx, int my) {
		SpriteManager sm = menu.sprite;
		SpriteManager.ScreenRenderer sr = sm.getRenderer(this);
		sr.start(stack);
	}

}
