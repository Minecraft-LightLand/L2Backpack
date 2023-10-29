package dev.xkmc.l2backpack.init.data;

import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.xkmc.l2backpack.compat.GolemCompat;
import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;

public class TagGen {


	public static final TagKey<Item> BACKPACKS = ItemTags.create(new ResourceLocation(L2Backpack.MODID, "backpacks"));
	public static final TagKey<Item> BAGS = ItemTags.create(new ResourceLocation(L2Backpack.MODID, "bags"));
	public static final TagKey<Item> DRAWERS = ItemTags.create(new ResourceLocation(L2Backpack.MODID, "drawers"));
	public static final TagKey<Item> SWAPS = ItemTags.create(new ResourceLocation(L2Backpack.MODID, "swaps"));
	public static final TagKey<Item> ENDER_CHEST = ItemTags.create(new ResourceLocation(L2Backpack.MODID, "ender_chest_access"));
	public static final TagKey<Item> DIMENSIONAL_STORAGES = ItemTags.create(new ResourceLocation(L2Backpack.MODID, "dimensional_storages"));

	public static void onBlockTagGen(RegistrateTagsProvider.IntrinsicImpl<Block> pvd) {
		if (ModList.get().isLoaded("modulargolems")) {
			GolemCompat.genBlockTag(pvd);
		}
	}

	public static void onItemTagGen(RegistrateTagsProvider.IntrinsicImpl<Item> pvd) {
	}

}
