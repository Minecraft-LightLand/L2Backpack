package dev.xkmc.l2backpack.content.quickswap.handswap;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.common.SimpleMenuPvd;
import dev.xkmc.l2backpack.content.remote.player.EnderBackpackItem;
import dev.xkmc.l2backpack.content.remote.player.EnderSyncCap;
import dev.xkmc.l2backpack.init.data.LangData;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import dev.xkmc.l2backpack.init.registrate.LBMisc;
import dev.xkmc.l2screentracker.screen.source.PlayerSlot;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HandswapItem extends BaseBagItem {

	public static ItemStack getToken(LivingEntity user) {
		List<ItemStack> list = new ArrayList<>();
		list.add(user.getItemBySlot(EquipmentSlot.LEGS));
		list.add(CuriosCompat.getItem(user, BackpackItems.HANDSWAP.asItem()));
		list.add(CuriosCompat.getItem(user, BackpackItems.ENDER_BACKPACK.asItem()));
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
			if (player.level().isClientSide()) {
				var inv = LBMisc.ENDER_SYNC.type().getOrCreate(player).getItems(player);
				for (ItemStack stack : inv) {
					if (stack.getItem() instanceof HandswapItem) {
						return stack;
					}
				}
			} else {
				var inv = player.getEnderChestInventory();
				for (int i = 0; i < inv.getContainerSize(); i++) {
					var stack = inv.getItem(i);
					if (stack.getItem() instanceof HandswapItem) {
						return stack;
					}
				}
			}
		}
		return ItemStack.EMPTY;
	}

	public static List<Item> getMatcher(ItemStack stack) {
		List<Item> ans = new ArrayList<>();
		var tag = stack.getOrCreateTag();
		if (tag.contains("MatcherItem", Tag.TAG_LIST)) {
			var list = stack.getOrCreateTag().getList("MatcherItem", Tag.TAG_STRING);
			for (int i = 0; i < list.size(); i++) {
				var str = list.getString(i);
				var id = ResourceLocation.tryParse(str);
				if (id != null) {
					var item = ForgeRegistries.ITEMS.getValue(id);
					ans.add(item);
				} else ans.add(Items.AIR);
			}
		}
		while (ans.size() < 9) ans.add(Items.AIR);
		return ans;
	}

	public static void setMatcher(ItemStack stack, List<Item> list) {
		ListTag tag = new ListTag();
		for (int i = 0; i < list.size(); i++) {
			var id = list.get(i).builtInRegistryHolder().unwrapKey().orElseThrow().location();
			tag.add(i, StringTag.valueOf(id.toString()));
		}
		stack.getOrCreateTag().put("MatcherItem", tag);
	}

	public static void check(ItemStack stack, Player pl) {
		int sel = pl.getInventory().selected;
		int old = stack.getOrCreateTag().getInt("selected");
		if (sel == old) return;
		var list = BaseBagItem.getItems(stack);
		while (list.size() < 9) {
			list.add(ItemStack.EMPTY);
		}
		var matcher = getMatcher(stack);

		if (matcher.get(old) != Items.AIR) {
			var current = pl.getOffhandItem();
			if (matcher.get(old) == current.getItem() && current.getItem().canFitInsideContainerItems()) {
				var prev = list.get(old);
				list.set(old, pl.getOffhandItem());
				pl.setItemInHand(InteractionHand.OFF_HAND, prev);
				matcher.set(old, Items.AIR);
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
			matcher.set(sel, item.getItem());
		}
		stack.getOrCreateTag().putInt("selected", sel);
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
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		PickupConfig.addText(stack, list);
		LangData.addInfo(list,
				LangData.Info.HANDSWAP,
				LangData.Info.INHERIT);
	}


	@Override
	public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
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
