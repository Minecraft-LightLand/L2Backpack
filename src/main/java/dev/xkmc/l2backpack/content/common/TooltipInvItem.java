package dev.xkmc.l2backpack.content.common;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface TooltipInvItem {

	int getInvSize(ItemStack stack);

	List<ItemStack> getInvItems(ItemStack stack, Player player);

}
