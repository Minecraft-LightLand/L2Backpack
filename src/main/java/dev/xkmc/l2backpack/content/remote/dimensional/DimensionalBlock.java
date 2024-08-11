package dev.xkmc.l2backpack.content.remote.dimensional;

import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.content.tool.TweakerTool;
import dev.xkmc.l2backpack.init.registrate.LBBlocks;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2modularblock.mult.*;
import dev.xkmc.l2modularblock.one.BlockEntityBlockMethod;
import dev.xkmc.l2modularblock.one.GetBlockItemBlockMethod;
import dev.xkmc.l2modularblock.one.ShapeBlockMethod;
import dev.xkmc.l2modularblock.one.SpecialDropBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class DimensionalBlock implements CreateBlockStateBlockMethod, DefaultStateBlockMethod, PlacementBlockMethod,
		UseItemOnBlockMethod, GetBlockItemBlockMethod, SpecialDropBlockMethod, SetPlacedByBlockMethod, ShapeBlockMethod {

	public static final DimensionalBlock INSTANCE = new DimensionalBlock();

	protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

	public static final BlockEntityBlockMethod<DimensionalBlockEntity> TILE_ENTITY_SUPPLIER_BUILDER =
			new DimensionalAnalogBlockEntity<>(LBBlocks.TE_DIMENSIONAL, DimensionalBlockEntity.class);

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
	public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof DimensionalBlockEntity chest) {
			if (stack.getItem() instanceof TweakerTool) {
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			}
			if (stack.getItem() instanceof DyeItem dye) {
				if (!level.isClientSide()) {
					level.setBlockAndUpdate(pos, state.setValue(COLOR, dye.getDyeColor()));
					chest.setColor(dye.getDyeColor().getId());
				} else {
					ContentTransfer.playSound(player);
				}
				return ItemInteractionResult.SUCCESS;
			}
			BlockPos blockpos = pos.above();
			if (level.getBlockState(blockpos).isRedstoneConductor(level, blockpos)) {
				return ItemInteractionResult.sidedSuccess(level.isClientSide);
			} else if (level.isClientSide) {
				ContentTransfer.playSound(player);
				return ItemInteractionResult.SUCCESS;
			} else {
				player.openMenu(chest);
				PiglinAi.angerNearbyPiglins(player, true);
				return ItemInteractionResult.CONSUME;
			}
		} else {
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		}
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof DimensionalBlockEntity chest) {
			return buildStack(state, chest);
		}
		return LBItems.DIMENSIONAL_STORAGE[state.getValue(COLOR).getId()].asStack();
	}

	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		BlockEntity blockentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockentity instanceof DimensionalBlockEntity chest) {
			return List.of(buildStack(state, chest));
		}
		return List.of(LBItems.DIMENSIONAL_STORAGE[state.getValue(COLOR).getId()].asStack());
	}

	public static ItemStack buildStack(BlockState state, DimensionalBlockEntity chest) {
		ItemStack stack = LBItems.DIMENSIONAL_STORAGE[state.getValue(COLOR).getId()].asStack();
		if (chest.ownerId != null) {
			stack.set(LBItems.DC_OWNER_ID, chest.ownerId);
			stack.set(LBItems.DC_OWNER_NAME, chest.ownerName);
			stack.set(LBItems.DC_PASSWORD, chest.password);
			stack.set(LBItems.DC_PICKUP, chest.config);
		}
		if (chest.name != null)
			stack.set(DataComponents.CUSTOM_NAME, chest.name);
		return stack;
	}

	@Override
	public BlockState getStateForPlacement(BlockState def, BlockPlaceContext context) {
		return def.setValue(COLOR, ((DimensionalItem) context.getItemInHand().getItem()).color);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof DimensionalBlockEntity chest) {
			chest.setColor(state.getValue(COLOR).getId());
			chest.addToListener();
		}
	}

	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

}