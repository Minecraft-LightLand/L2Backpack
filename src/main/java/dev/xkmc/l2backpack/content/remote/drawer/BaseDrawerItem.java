package dev.xkmc.l2backpack.content.remote.drawer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public abstract class BaseDrawerItem extends Item {

	public static final String KEY = "drawerItem";

	public BaseDrawerItem(Properties properties) {
		super(properties);
	}

	public boolean canAccept(ItemStack drawer, ItemStack stack) {
		return !stack.hasTag() && !stack.isEmpty() && stack.getItem() == getItem(drawer);
	}

	public Item getItem(ItemStack stack) {
		return Optional.ofNullable(stack.getTag())
				.map(e -> e.contains(KEY) ? e.getString(KEY) : null)
				.map(e -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(e)))
				.orElse(Items.AIR);
	}
}
