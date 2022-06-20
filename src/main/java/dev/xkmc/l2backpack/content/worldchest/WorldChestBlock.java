package dev.xkmc.l2backpack.content.worldchest;

import dev.xkmc.l2library.block.mult.*;
import dev.xkmc.l2backpack.init.registrate.BackpackBlocks;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import dev.xkmc.l2library.block.impl.BlockEntityBlockMethodImpl;
import dev.xkmc.l2library.block.one.BlockEntityBlockMethod;
import dev.xkmc.l2library.block.one.GetBlockItemBlockMethod;
import dev.xkmc.l2library.block.one.SpecialDropBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;
import java.util.UUID;

public class WorldChestBlock implements CreateBlockStateBlockMethod, DefaultStateBlockMethod, PlacementBlockMethod,
		OnClickBlockMethod, GetBlockItemBlockMethod, SpecialDropBlockMethod, SetPlacedByBlockMethod {

	public static final WorldChestBlock INSTANCE = new WorldChestBlock();

	public static final BlockEntityBlockMethod<WorldChestBlockEntity> TILE_ENTITY_SUPPLIER_BUILDER =
			new BlockEntityBlockMethodImpl<>(BackpackBlocks.TE_WORLD_CHEST, WorldChestBlockEntity.class);

	public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(COLOR);
	}

	@Override
	public BlockState getDefaultState(BlockState state) {
		return state.setValue(COLOR, DyeColor.WHITE);
	}

	@Override
	public InteractionResult onClick(BlockState state, Level level, BlockPos pos, Player pl, InteractionHand hand, BlockHitResult result) {
		ItemStack stack = pl.getItemInHand(hand);
		BlockEntity be = level.getBlockEntity(pos);
		if (stack.getItem() instanceof DyeItem dye && be instanceof WorldChestBlockEntity chest) {
			if (!level.isClientSide()) {
				level.setBlockAndUpdate(pos, state.setValue(COLOR, dye.getDyeColor()));
				chest.setColor(dye.getDyeColor().getId());
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof WorldChestBlockEntity chest) {
			return buildStack(state, chest);
		}
		return BackpackItems.DIMENSIONAL_STORAGE[state.getValue(COLOR).getId()].asStack();
	}

	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		BlockEntity blockentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockentity instanceof WorldChestBlockEntity chest) {
			return List.of(buildStack(state, chest));
		}
		return List.of(BackpackItems.DIMENSIONAL_STORAGE[state.getValue(COLOR).getId()].asStack());
	}

	private ItemStack buildStack(BlockState state, WorldChestBlockEntity chest) {
		ItemStack stack = BackpackItems.DIMENSIONAL_STORAGE[state.getValue(COLOR).getId()].asStack();
		stack.getOrCreateTag().putUUID("owner_id", chest.owner_id);
		stack.getOrCreateTag().putString("owner_name", chest.owner_name);
		stack.getOrCreateTag().putLong("password", chest.password);
		return stack;
	}

	@Override
	public BlockState getStateForPlacement(BlockState def, BlockPlaceContext context) {
		return def.setValue(COLOR, ((WorldChestItem) context.getItemInHand().getItem()).color);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		UUID id = stack.getOrCreateTag().getUUID("owner_id");
		String name = stack.getOrCreateTag().getString("owner_name");
		long pwd = stack.getOrCreateTag().getLong("password");
		if (blockentity instanceof WorldChestBlockEntity chest) {
			chest.owner_id = id;
			chest.owner_name = name;
			chest.password = pwd;
			chest.setColor(state.getValue(COLOR).getId());
		}
	}
}
