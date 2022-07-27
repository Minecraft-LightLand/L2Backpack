package dev.xkmc.l2backpack.init.registrate;

import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestBlock;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestBlockEntity;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestRenderer;
import dev.xkmc.l2library.repack.registrate.util.entry.BlockEntityEntry;
import dev.xkmc.l2library.repack.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static dev.xkmc.l2backpack.init.L2Backpack.REGISTRATE;

/**
 * handles blocks and block entities
 */
public class BackpackBlocks {

	static {
		REGISTRATE.creativeModeTab(() -> BackpackItems.TAB_MAIN);
	}

	public static final BlockEntry<WorldChestBlock> WORLD_CHEST;
	public static final BlockEntityEntry<WorldChestBlockEntity> TE_WORLD_CHEST;

	static {
		WORLD_CHEST = REGISTRATE.block("dimensional_storage", p -> new WorldChestBlock(BlockBehaviour.Properties.copy(Blocks.ENDER_CHEST)))
				.blockstate((ctx, pvd) -> pvd.simpleBlock(ctx.getEntry(), pvd.models()
						.getExistingFile(new ResourceLocation("block/ender_chest"))))
				.loot((table, block) -> table.dropOther(block, Blocks.ENDER_CHEST))
				.tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL)
				.defaultLang().register();
		TE_WORLD_CHEST = REGISTRATE.blockEntity("dimensional_storage", WorldChestBlockEntity::new)
				.validBlock(WORLD_CHEST).renderer(() -> WorldChestRenderer::new).register();

	}

	public static void register() {
	}

}
