package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.quickswap.scabbard.Scabbard;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class QuickSwapManager {

	@Nullable
	public static QuickSwapType getValidType(Player player, boolean isAltDown) {
		if (!isAltDown && player.getMainHandItem().isEmpty()) {
			return QuickSwapType.ARMOR;
		}
		if (player.getMainHandItem().getItem() instanceof ProjectileWeaponItem) {
			return QuickSwapType.ARROW;
		}
		if (isAltDown && player.getMainHandItem().isEmpty() ||
				Scabbard.isValidItem(player.getMainHandItem())) {
			return QuickSwapType.TOOL;
		}
		return null;
	}

	@Nullable
	public static IQuickSwapToken getToken(Player player, boolean isAltDown) {
		List<ItemStack> list = new ArrayList<>();
		list.add(player.getOffhandItem());
		list.add(player.getItemBySlot(EquipmentSlot.CHEST));
		list.add(CuriosCompat.getSlot(player, stack -> stack.getItem() instanceof IQuickSwapItem));
		QuickSwapType type = getValidType(player, isAltDown);
		if (type == null)
			return null;
		for (ItemStack stack : list) {
			if (stack.getItem() instanceof IQuickSwapItem item) {
				IQuickSwapToken token = item.getTokenOfType(stack, player, type);
				if (token != null) {
					return token;
				}
			}
		}
		return null;
	}

}
