package dev.xkmc.l2backpack.content.drawer;

import dev.xkmc.l2backpack.content.capability.BackpackCap;
import dev.xkmc.l2backpack.content.capability.PickupBagItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public interface BaseDrawerItem extends PickupBagItem {

	String KEY = "drawerItem", STACKING = "StackingFactor";

	int MAX_FACTOR = 8;

	static boolean canAccept(ItemStack drawer, ItemStack stack) {
		return !stack.hasTag() && !stack.isEmpty() && stack.getItem() == getItem(drawer);
	}

	static Item getItem(ItemStack drawer) {
		return Optional.ofNullable(drawer.getTag())
				.map(e -> e.contains(KEY) ? e.getString(KEY) : null)
				.map(e -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(e)))
				.orElse(Items.AIR);
	}

	static int loadFromInventory(int max, int count, Item item, Player player) {
		int ext = 0;
		for (int i = 0; i < 36; i++) {
			ItemStack inv_stack = player.getInventory().items.get(i);
			if (inv_stack.getItem() == item && !inv_stack.hasTag()) {
				int take = Math.min(max - count, inv_stack.getCount());
				count += take;
				ext += take;
				inv_stack.shrink(take);
				if (count == max) break;
			}
		}
		return ext;
	}

	static int getStacking(ItemStack drawer) {
		return getStacking(BackpackCap.getConfig(drawer));
	}

	static int getStacking(CompoundTag tag) {
		int factor = tag.getInt(STACKING);
		if (factor < 1) factor = 1;
		return getStacking() * factor;
	}

	static int getStackingFactor(ItemStack drawer) {
		int factor = BackpackCap.getConfig(drawer).getInt(STACKING);
		if (factor < 1) factor = 1;
		return factor;
	}

	static ItemStack setStackingFactor(ItemStack drawer, int factor) {
		BackpackCap.getConfig(drawer).putInt(STACKING, factor);
		return drawer;
	}

	static int getStacking() {
		return 64;
	}

	void insert(ItemStack drawer, ItemStack stack, Player player);

	default void setItem(ItemStack drawer, Item item, Player player) {
		ResourceLocation rl = ForgeRegistries.ITEMS.getKey(item);
		assert rl != null;
		drawer.getOrCreateTag().putString(KEY, rl.toString());
	}

	default ItemStack takeItem(ItemStack drawer, Player player) {
		return takeItem(drawer, Integer.MAX_VALUE, player, false);
	}

	ItemStack takeItem(ItemStack drawer, int max, Player player, boolean simulate);

	boolean canSetNewItem(ItemStack drawer);

}
