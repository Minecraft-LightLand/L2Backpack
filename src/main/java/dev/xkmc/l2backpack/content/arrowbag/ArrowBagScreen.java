package dev.xkmc.l2backpack.content.arrowbag;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2backpack.content.common.BaseBagScreen;
import dev.xkmc.l2library.base.menu.BaseContainerScreen;
import dev.xkmc.l2library.base.menu.SpriteManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ArrowBagScreen extends BaseBagScreen<ArrowBagContainer> {

	public ArrowBagScreen(ArrowBagContainer cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

}
