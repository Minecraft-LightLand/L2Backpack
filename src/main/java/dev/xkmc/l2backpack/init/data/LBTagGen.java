package dev.xkmc.l2backpack.init.data;

import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.xkmc.l2backpack.compat.GolemCompat;
import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;

public class LBTagGen {


	public static final TagKey<Item> BACKPACKS = ItemTags.create(L2Backpack.loc("backpacks"));
	public static final TagKey<Item> BAGS = ItemTags.create(L2Backpack.loc("bags"));
	public static final TagKey<Item> DRAWERS = ItemTags.create(L2Backpack.loc("drawers"));
	public static final TagKey<Item> SWAPS = ItemTags.create(L2Backpack.loc("swaps"));
	public static final TagKey<Item> ENDER_CHEST = ItemTags.create(L2Backpack.loc("ender_chest_access"));
	public static final TagKey<Item> DIMENSIONAL_STORAGES = ItemTags.create(L2Backpack.loc("dimensional_storages"));
	public static final TagKey<Item> BACKPACK_BLACKLIST = ItemTags.create(L2Backpack.loc("backpack_blacklist"));

	public static void onBlockTagGen(RegistrateTagsProvider.IntrinsicImpl<Block> pvd) {
		if (ModList.get().isLoaded("modulargolems")) {
			GolemCompat.genBlockTag(pvd);
		}
	}

	public static void onItemTagGen(RegistrateTagsProvider.IntrinsicImpl<Item> pvd) {
		pvd.addTag(BACKPACK_BLACKLIST);
	}

}
