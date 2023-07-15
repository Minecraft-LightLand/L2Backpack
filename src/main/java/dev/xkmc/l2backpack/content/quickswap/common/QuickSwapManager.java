package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.quickswap.scabbard.Scabbard;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class QuickSwapManager {

	@Nullable
	public static QuickSwapType getValidType(LivingEntity player, boolean isAltDown) {
		if (player.getMainHandItem().getItem() instanceof ProjectileWeaponItem) {
			return QuickSwapType.ARROW;
		}
		if (player.getOffhandItem().getItem() instanceof ProjectileWeaponItem) {
			return QuickSwapType.ARROW;
		}
		if (!isAltDown && player.getMainHandItem().isEmpty()) {
			return QuickSwapType.ARMOR;
		}
		if (isAltDown && player.getMainHandItem().isEmpty() ||
				Scabbard.isValidItem(player.getMainHandItem())) {
			return QuickSwapType.TOOL;
		}
		return null;
	}

	@Nullable
	public static IQuickSwapToken getToken(LivingEntity user, boolean isAltDown) {
		List<ItemStack> list = new ArrayList<>();
		list.add(user.getOffhandItem());
		list.add(user.getItemBySlot(EquipmentSlot.CHEST));
		if (user instanceof Player pl) {
			var opt = CuriosCompat.getSlot(pl, stack -> stack.getItem() instanceof IQuickSwapItem);
			opt.ifPresent(pair -> list.add(pair.getFirst()));
		}
		QuickSwapType type = getValidType(user, isAltDown);
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
