package dev.xkmc.l2backpack.content.worldchest;

import dev.xkmc.l2backpack.init.registrate.BackpackBlocks;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class WorldChestBlock extends AbstractChestBlock<WorldChestBlockEntity> implements SimpleWaterloggedBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);
	protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

	public WorldChestBlock(BlockBehaviour.Properties properties) {
		super(properties, BackpackBlocks.TE_WORLD_CHEST::get);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(WATERLOGGED, Boolean.FALSE)
				.setValue(COLOR, DyeColor.WHITE));
	}

	public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState p_53149_, Level p_53150_, BlockPos p_53151_, boolean p_53152_) {
		return DoubleBlockCombiner.Combiner::acceptNone;
	}

	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER)
				.setValue(COLOR, ((WorldChestItem) context.getItemInHand().getItem()).color);
	}

	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof WorldChestBlockEntity chest) {
			ItemStack stack = player.getItemInHand(hand);
			if (stack.getItem() instanceof DyeItem dye) {
				if (!level.isClientSide()) {
					level.setBlockAndUpdate(pos, state.setValue(COLOR, dye.getDyeColor()));
					chest.setColor(dye.getDyeColor().getId());
				}
				return InteractionResult.SUCCESS;
			}
			BlockPos blockpos = pos.above();
			if (level.getBlockState(blockpos).isRedstoneConductor(level, blockpos)) {
				return InteractionResult.sidedSuccess(level.isClientSide);
			} else if (level.isClientSide) {
				return InteractionResult.SUCCESS;
			} else {
				player.openMenu(chest);
				PiglinAi.angerNearbyPiglins(player, true);
				return InteractionResult.CONSUME;
			}
		} else {
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
	}

	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new WorldChestBlockEntity(BackpackBlocks.TE_WORLD_CHEST.get(), pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide ? createTickerHelper(type, BackpackBlocks.TE_WORLD_CHEST.get(), WorldChestBlockEntity::lidAnimateTick) : null;
	}

	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource source) {
		for (int i = 0; i < 3; ++i) {
			int j = source.nextInt(2) * 2 - 1;
			int k = source.nextInt(2) * 2 - 1;
			double d0 = pos.getX() + 0.5D + 0.25D * j;
			double d1 = pos.getY() + source.nextFloat();
			double d2 = pos.getZ() + 0.5D + 0.25D * k;
			double d3 = source.nextFloat() * j;
			double d4 = (source.nextFloat() - 0.5D) * 0.125D;
			double d5 = source.nextFloat() * k;
			level.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
		}

	}

	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED, COLOR);
	}

	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	public BlockState updateShape(BlockState state, Direction direction, BlockState nextState,
								  LevelAccessor levelAccessor, BlockPos pos, BlockPos nextPos) {
		if (state.getValue(WATERLOGGED)) {
			levelAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
		}

		return super.updateShape(state, direction, nextState, levelAccessor, pos, nextPos);
	}

	public boolean isPathfindable(BlockState state, BlockGetter getter, BlockPos pos, PathComputationType type) {
		return false;
	}

	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource source) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof WorldChestBlockEntity chest) {
			chest.recheckOpen();
		}
	}

	// new

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		UUID id = stack.getOrCreateTag().getUUID("owner_id");
		String name = stack.getOrCreateTag().getString("owner_name");
		long pwd = stack.getOrCreateTag().getLong("password");
		if (blockentity instanceof WorldChestBlockEntity chest) {
			chest.owner_id = id;
			chest.owner_name = name;
			chest.password = pwd;
			chest.setColor(state.getValue(COLOR).getId());
			if (stack.hasCustomHoverName()) {
				chest.setCustomName(stack.getHoverName());
			}
		}
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
		if (chest.owner_id != null) {
			stack.getOrCreateTag().putUUID("owner_id", chest.owner_id);
			stack.getOrCreateTag().putString("owner_name", chest.owner_name);
			stack.getOrCreateTag().putLong("password", chest.password);
		}
		return stack;
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof WorldChestBlockEntity chest) {
			return buildStack(state, chest);
		}
		return BackpackItems.DIMENSIONAL_STORAGE[state.getValue(COLOR).getId()].asStack();
	}

}