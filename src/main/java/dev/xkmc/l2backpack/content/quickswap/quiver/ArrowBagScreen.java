package dev.xkmc.l2backpack.content.quickswap.quiver;

import dev.xkmc.l2backpack.content.common.BaseOpenableScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ArrowBagScreen extends BaseOpenableScreen<ArrowBagContainer> {

	public ArrowBagScreen(ArrowBagContainer cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

}
