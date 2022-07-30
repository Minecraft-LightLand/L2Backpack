package dev.xkmc.l2backpack.init.registrate;

import dev.xkmc.l2backpack.content.remote.EnderParticleBlock;
import dev.xkmc.l2backpack.content.remote.drawer.AlternateBlockForm;
import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerBlock;
import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerBlockEntity;
import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerRenderer;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestBlock;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestBlockEntity;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2library.block.BlockProxy;
import dev.xkmc.l2library.block.DelegateBlock;
import dev.xkmc.l2library.block.DelegateBlockProperties;
import dev.xkmc.l2library.repack.registrate.util.entry.BlockEntityEntry;
import dev.xkmc.l2library.repack.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

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

	public static final BlockEntry<DelegateBlock> ENDER_DRAWER;
	public static final BlockEntityEntry<EnderDrawerBlockEntity> TE_ENDER_DRAWER;

	static {
		WORLD_CHEST = REGISTRATE.block("dimensional_storage", p -> DelegateBlock.newBaseBlock(DelegateBlockProperties.copy(Blocks.ENDER_CHEST),
						BlockProxy.HORIZONTAL, WorldChestBlock.INSTANCE, EnderParticleBlock.INSTANCE,
						WorldChestBlock.TILE_ENTITY_SUPPLIER_BUILDER))
				.blockstate((ctx, pvd) -> pvd.horizontalBlock(ctx.getEntry(), state -> pvd.models()
						.withExistingParent(ctx.getName() + "_" + state.getValue(WorldChestBlock.COLOR).getName(),
								new ResourceLocation(L2Backpack.MODID, "backpack"))
						.texture("0", "block/dimensional_storage/" + state.getValue(WorldChestBlock.COLOR).getName())))
				.loot((table, block) -> table.dropOther(block, Blocks.ENDER_CHEST))
				.tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL)
				.defaultLang().register();
		TE_WORLD_CHEST = REGISTRATE.blockEntity("dimensional_storage", WorldChestBlockEntity::new)
				.validBlock(WORLD_CHEST).register();

		ENDER_DRAWER = REGISTRATE.block("ender_drawer", p -> DelegateBlock.newBaseBlock(
						DelegateBlockProperties.copy(Blocks.ENDER_CHEST).make(e -> e.noOcclusion()),
						BlockProxy.HORIZONTAL, EnderDrawerBlock.INSTANCE, EnderParticleBlock.INSTANCE,
						EnderDrawerBlock.TILE_ENTITY_SUPPLIER_BUILDER, AlternateBlockForm.INSTANCE))
				.blockstate((ctx, pvd) -> pvd.horizontalBlock(ctx.getEntry(), state ->
						pvd.models().withExistingParent(ctx.getName(), new ResourceLocation(L2Backpack.MODID, "block/drawer"))
								.texture("0", "block/drawer/" + (state.getValue(AlternateBlockForm.ALT) ? "drawer" : "ender_drawer"))
								.renderType("cutout")))
				.loot((table, block) -> table.dropOther(block, Blocks.ENDER_CHEST))
				.tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL)
				.defaultLang().register();

		TE_ENDER_DRAWER = REGISTRATE.blockEntity("ender_drawer", EnderDrawerBlockEntity::new)
				.validBlock(ENDER_DRAWER).renderer(() -> EnderDrawerRenderer::new).register();

	}

	public static void register() {
	}

}
