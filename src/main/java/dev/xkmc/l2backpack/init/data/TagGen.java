package dev.xkmc.l2backpack.init.data;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TagGen {


	public static final TagKey<Item> BACKPACKS = ItemTags.create(new ResourceLocation(L2Backpack.MODID, "backpacks"));
	public static final TagKey<Item> DIMENSIONAL_STORAGES = ItemTags.create(new ResourceLocation(L2Backpack.MODID, "dimensional_storages"));

	public static void onBlockTagGen(RegistrateTagsProvider<Block> pvd) {
	}

	public static void onItemTagGen(RegistrateTagsProvider<Item> pvd) {
	}

}
