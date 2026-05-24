package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public interface IQuickSwapItem {

	@Nullable
	IQuickSwapToken<?> getTokenOfType(ItemStack stack, LivingEntity player, QuickSwapType type);

	default List<IQuickSwapToken<?>> getAllTokensOfType(ItemStack stack, LivingEntity user, QuickSwapType t) {
		var ans = getTokenOfType(stack, user, t);
		return ans == null ? List.of() : List.of(ans);
	}

}
