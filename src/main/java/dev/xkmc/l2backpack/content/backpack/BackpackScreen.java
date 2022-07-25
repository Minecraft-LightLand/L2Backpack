package dev.xkmc.l2backpack.content.backpack;

import dev.xkmc.l2backpack.content.common.BaseBagScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BackpackScreen extends BaseBagScreen<BackpackContainer> {

	public BackpackScreen(BackpackContainer cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

}
