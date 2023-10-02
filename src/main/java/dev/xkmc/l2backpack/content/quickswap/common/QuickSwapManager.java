package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.quickswap.scabbard.Scabbard;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class QuickSwapManager {

	@Nullable
	public static QuickSwapType getValidType(LivingEntity player, boolean isAltDown) {
		QuickSwapType main = getValidType(player, player.getMainHandItem(), isAltDown);
		if (main != null) {
			return main;
		}
		if (player.getOffhandItem().getItem() instanceof ProjectileWeaponItem)
			return getValidType(player, player.getOffhandItem(), isAltDown);
		return null;
	}

	@Nullable
	public static QuickSwapType getValidType(LivingEntity player, ItemStack focus, boolean isAltDown) {
		if (isAltDown && Scabbard.isValidItem(focus)) {
			return QuickSwapType.TOOL;
		}
		if (focus.getItem() instanceof ProjectileWeaponItem) {
			return QuickSwapType.ARROW;
		}
		if (isAltDown && focus.isEmpty() || Scabbard.isValidItem(focus)) {
			return QuickSwapType.TOOL;
		}
		if (focus.isEmpty()) {
			return QuickSwapType.ARMOR;
		}
		return null;
	}

	@Nullable
	public static IQuickSwapToken getToken(LivingEntity user, boolean isAltDown) {
		return getToken(user, null, isAltDown);
	}

	@Nullable
	public static IQuickSwapToken getToken(LivingEntity user, @Nullable ItemStack focus, boolean isAltDown) {
		List<ItemStack> list = new ArrayList<>();
		list.add(user.getOffhandItem());
		list.add(user.getItemBySlot(EquipmentSlot.CHEST));
		var opt = CuriosCompat.getSlot(user, stack -> stack.getItem() instanceof IQuickSwapItem);
		opt.ifPresent(pair -> list.add(pair.getFirst()));
		QuickSwapType type = focus == null ? getValidType(user, isAltDown) : getValidType(user, focus, isAltDown);
		if (type == null)
			return null;
		for (ItemStack stack : list) {
			if (stack.getItem() instanceof IQuickSwapItem item) {
				IQuickSwapToken token = item.getTokenOfType(stack, user, type);
				if (token != null) {
					return token;
				}
			}
		}
		return null;
	}

}
