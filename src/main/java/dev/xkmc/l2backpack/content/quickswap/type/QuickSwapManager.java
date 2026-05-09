package dev.xkmc.l2backpack.content.quickswap.type;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapItem;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.single.Scabbard;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class QuickSwapManager {

	public static LinkedHashSet<QuickSwapType> getValidType(LivingEntity player, boolean isAltDown) {
		if (!isAltDown) {
			LinkedHashSet<QuickSwapType> ans = new LinkedHashSet<>();
			for (var e : QuickSwapTypes.MATCHER) {
				if (e.match(player.getMainHandItem())) {
					ans.add(e);
				}
			}
			for (var e : QuickSwapTypes.MATCHER) {
				if (e.allowsOffhand() && e.match(player.getOffhandItem())) {
					ans.add(e);
				}
			}
			if (Scabbard.isValidItem(player.getMainHandItem())) {
				ans.add(QuickSwapTypes.TOOL);
			}
			ans.add(QuickSwapTypes.ARMOR);
			ans.add(QuickSwapTypes.TOOL);
			return ans;
		} else {
			LinkedHashSet<QuickSwapType> ans = getValidType(player, player.getMainHandItem(), isAltDown);
			for (var e : QuickSwapTypes.MATCHER) {
				if (e.allowsOffhand() && e.match(player.getOffhandItem())) {
					ans.add(e);
				}
			}
			return ans;
		}
	}

	public static LinkedHashSet<QuickSwapType> getValidType(LivingEntity player, ItemStack focus, boolean isAltDown) {
		LinkedHashSet<QuickSwapType> ans = new LinkedHashSet<>();
		if (isAltDown && Scabbard.isValidItem(focus)) {
			ans.add(QuickSwapTypes.TOOL);
		}
		for (var e : QuickSwapTypes.MATCHER) {
			if (e.match(focus)) {
				ans.add(e);
			}
		}
		if (isAltDown && focus.isEmpty() || Scabbard.isValidItem(focus)) {
			ans.add(QuickSwapTypes.TOOL);
		}
		ans.add(QuickSwapTypes.ARMOR);
		ans.add(QuickSwapTypes.TOOL);
		return ans;
	}

	@Nullable
	public static IQuickSwapToken<?> getToken(LivingEntity user, boolean isAltDown) {
		return getToken(user, null, isAltDown);
	}

	@Nullable
	public static IQuickSwapToken<?> getToken(LivingEntity user, @Nullable ItemStack focus, boolean isAltDown) {
		var list = getTokens(user, focus, isAltDown);
		return list.isEmpty() ? null : list.getFirst();
	}

	public static List<IQuickSwapToken<?>> getTokens(LivingEntity user, @Nullable ItemStack focus, boolean isAltDown) {
		List<ItemStack> list = new ArrayList<>();
		list.add(user.getMainHandItem());
		list.add(user.getOffhandItem());
		list.add(user.getItemBySlot(EquipmentSlot.CHEST));
		var opt = CuriosCompat.getSlot(user, stack -> stack.getItem() instanceof IQuickSwapItem);
		opt.ifPresent(pair -> list.add(pair.getFirst()));
		LinkedHashSet<QuickSwapType> type = focus == null ? getValidType(user, isAltDown) : getValidType(user, focus, isAltDown);
		if (type.isEmpty())
			return List.of();
		List<IQuickSwapToken<?>> ans = new ArrayList<>();
		for (var t : type) {
			for (ItemStack stack : list) {
				if (stack.getItem() instanceof IQuickSwapItem item) {
					IQuickSwapToken<?> token = item.getTokenOfType(stack, user, t);
					if (token != null) {
						ans.add(token);
						break;
					}
				}
			}
		}
		return ans;
	}

}
