package dev.xkmc.l2backpack.content.quickswap.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface IQuickSwapItem {

	@Nullable
	IQuickSwapToken getTokenOfType(ItemStack stack, LivingEntity player, QuickSwapType type);

}
