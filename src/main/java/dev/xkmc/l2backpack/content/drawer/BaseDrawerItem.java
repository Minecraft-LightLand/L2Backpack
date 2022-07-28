package dev.xkmc.l2backpack.content.drawer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public interface BaseDrawerItem {

	String KEY = "drawerItem";

	static boolean canAccept(ItemStack drawer, ItemStack stack) {
		return !stack.hasTag() && !stack.isEmpty() && stack.getItem() == getItem(drawer);
	}

	static Item getItem(ItemStack drawer) {
		return Optional.ofNullable(drawer.getTag())
				.map(e -> e.contains(KEY) ? e.getString(KEY) : null)
				.map(e -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(e)))
				.orElse(Items.AIR);
	}

	void insert(ItemStack drawer, ItemStack stack, Player player);

	default void setItem(ItemStack drawer, Item item, Player player) {
		ResourceLocation rl = ForgeRegistries.ITEMS.getKey(item);
		assert rl != null;
		drawer.getOrCreateTag().putString(KEY, rl.toString());
	}

	ItemStack takeItem(ItemStack drawer, Player player);

	boolean canSetNewItem(ItemStack drawer);
}
