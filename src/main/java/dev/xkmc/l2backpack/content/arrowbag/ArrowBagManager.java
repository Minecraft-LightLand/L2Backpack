package dev.xkmc.l2backpack.content.arrowbag;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ArrowBagManager {

	public static ItemStack getArrowBag(Player player) {
		if (player.getOffhandItem().getItem() instanceof ArrowBag) {
			return player.getOffhandItem();
		}
		if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArrowBag) {
			return player.getItemBySlot(EquipmentSlot.CHEST);
		}
		ItemStack curio = CuriosCompat.getSlot(player, stack -> stack.getItem() instanceof ArrowBag);
		if (!curio.isEmpty()) {
			return curio;
		}
		return ItemStack.EMPTY;
	}

}
