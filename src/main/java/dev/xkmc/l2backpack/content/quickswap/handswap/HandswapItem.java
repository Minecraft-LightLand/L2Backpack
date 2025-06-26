package dev.xkmc.l2backpack.content.quickswap.handswap;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.common.SimpleMenuPvd;
import dev.xkmc.l2backpack.content.quickswap.common.SingleSwapItem;
import dev.xkmc.l2backpack.content.remote.player.EnderBackpackItem;
import dev.xkmc.l2backpack.init.data.LBLang;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HandswapItem extends BaseBagItem {

	public static ItemStack getToken(LivingEntity user) {
		List<ItemStack> list = new ArrayList<>();
		list.add(user.getItemBySlot(EquipmentSlot.LEGS));
		list.add(CuriosCompat.getItem(user, LBItems.HANDSWAP.asItem()));
		list.add(CuriosCompat.getItem(user, LBItems.ENDER_BACKPACK.asItem()));
		boolean ender = false;
		for (ItemStack stack : list) {
			if (stack.getItem() instanceof HandswapItem) {
				return stack;
			}
			if (stack.getItem() instanceof EnderBackpackItem) {
				ender = true;
			}
		}
		if (ender && user instanceof Player player) {
			var inv = player.getEnderChestInventory();
			for (int i = 0; i < inv.getContainerSize(); i++) {
				var stack = inv.getItem(i);
				if (stack.getItem() instanceof HandswapItem) {
					return stack;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	public static Item[] getMatcher(ItemStack stack) {
		return LBItems.DC_MATCHER.getOrDefault(stack, MatcherData.EMPTY).copy();
	}

	public static void setMatcher(ItemStack stack, Item[] list) {
		LBItems.DC_MATCHER.set(stack, new MatcherData(list.clone()));
	}

	public static void check(ItemStack stack, Player pl) {
		int sel = pl.getInventory().selected;
		int old = SingleSwapItem.getSelected(stack);
		if (sel == old) return;
		var list = BaseBagItem.getItems(stack);
		while (list.size() < 9) {
			list.add(ItemStack.EMPTY);
		}
		var matcher = getMatcher(stack);

		if (matcher[old] != Items.AIR) {
			var current = pl.getOffhandItem();
			if (matcher[old] == current.getItem() && current.getItem().canFitInsideContainerItems()) {
				var prev = list.get(old);
				list.set(old, pl.getOffhandItem());
				pl.setItemInHand(InteractionHand.OFF_HAND, prev);
				matcher[old] = Items.AIR;
			} else {
				var prev = list.get(old);
				list.set(old, ItemStack.EMPTY);
				pl.getInventory().placeItemBackInInventory(prev);
			}
		}
		if (!list.get(sel).isEmpty()) {
			var current = pl.getOffhandItem();
			var item = list.get(sel);
			pl.setItemInHand(InteractionHand.OFF_HAND, item);
			list.set(sel, current);
			matcher[sel] = item.getItem();
		}
		SingleSwapItem.setSelected(stack, sel);
		BaseBagItem.setItems(stack, list);
		setMatcher(stack, matcher);
	}

	public HandswapItem(Properties props) {
		super(props.stacksTo(1));
	}

	@Override
	public void open(ServerPlayer player, PlayerSlot<?> slot, ItemStack stack) {
		new SimpleMenuPvd(player, slot, this, stack, HandswapMenu::new).open();
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
		PickupConfig.addText(stack, list);
		LBLang.addInfo(flag, list,
				LBLang.Info.HANDSWAP,
				LBLang.Info.INHERIT);
	}

	@Override
	public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
		return armorType == EquipmentSlot.LEGS;
	}

	@Override
	public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return EquipmentSlot.LEGS;
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}

}
