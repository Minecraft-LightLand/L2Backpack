package dev.xkmc.l2backpack.content.common;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2backpack.content.restore.ScreenTrackerClient;
import dev.xkmc.l2library.base.menu.BaseContainerScreen;
import dev.xkmc.l2library.base.menu.SpriteManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BaseOpenableScreen<T extends BaseOpenableContainer<T>> extends BaseContainerScreen<T> {

	public BaseOpenableScreen(T cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
		ScreenTrackerClient.onClientOpen(Minecraft.getInstance().screen, getMenu().containerId, this);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void renderBg(PoseStack stack, float pt, int mx, int my) {
		SpriteManager sm = menu.sprite;
		SpriteManager.ScreenRenderer sr = sm.getRenderer(this);
		sr.start(stack);
	}

	public void onClose() {
		if (!ScreenTrackerClient.onClientClose(getMenu().containerId)) {
			super.onClose();
		}
	}

}
