package dev.xkmc.l2backpack.content.arrowbag;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ArrowBagManager {

	public static ItemStack getArrowBag(Player player) {
		if (player.getOffhandItem().getItem() instanceof ArrowBag) {
			return player.getOffhandItem();
		}
		return ItemStack.EMPTY;
	}

}
