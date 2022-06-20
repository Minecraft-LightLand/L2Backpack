package dev.xkmc.l2backpack.init.registrate;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2library.block.DelegateBlock;
import dev.xkmc.l2library.block.DelegateBlockProperties;
import dev.xkmc.l2library.repack.registrate.util.entry.BlockEntityEntry;
import dev.xkmc.l2library.repack.registrate.util.entry.BlockEntry;
import dev.xkmc.l2backpack.content.worldchest.WorldChestBlock;
import dev.xkmc.l2backpack.content.worldchest.WorldChestBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

import static dev.xkmc.l2backpack.init.L2Backpack.REGISTRATE;

/**
 * handles blocks and block entities
 */
public class BackpackBlocks {

	static {
		REGISTRATE.creativeModeTab(() -> BackpackItems.TAB_MAIN);
	}

	public static final BlockEntry<DelegateBlock> WORLD_CHEST;
	public static final BlockEntityEntry<WorldChestBlockEntity> TE_WORLD_CHEST;

	static {

		DelegateBlockProperties CHEST = DelegateBlockProperties.copy(Blocks.ENDER_CHEST);
		WORLD_CHEST = REGISTRATE.block("dimensional_storage", p -> DelegateBlock.newBaseBlock(
						CHEST, WorldChestBlock.INSTANCE, WorldChestBlock.TILE_ENTITY_SUPPLIER_BUILDER
				)).blockstate((ctx, pvd) -> {
					for (DyeColor color : DyeColor.values()) {
						pvd.models().cubeAll("dimensional_storage_" + color.getName(), new ResourceLocation(L2Backpack.MODID,
								"block/dimensional_storage_" + color.getName()));
					}
					pvd.getVariantBuilder(ctx.getEntry()).forAllStates(state ->
							ConfiguredModel.builder().modelFile(new ModelFile.UncheckedModelFile(new ResourceLocation(L2Backpack.MODID,
											"block/dimensional_storage_" + state.getValue(WorldChestBlock.COLOR).getName())))
									.build());
				}).loot((table, block) -> table.dropOther(block, Blocks.ENDER_CHEST))
				.tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL)
				.defaultLang().register();
		TE_WORLD_CHEST = REGISTRATE.blockEntity("dimensional_storage", WorldChestBlockEntity::new)
				.validBlock(WORLD_CHEST).register();

	}

	public static void register() {
	}

}
