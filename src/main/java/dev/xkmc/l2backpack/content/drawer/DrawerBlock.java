package dev.xkmc.l2backpack.content.drawer;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.content.tool.IBagTool;
import dev.xkmc.l2backpack.init.registrate.LBBlocks;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2core.util.DCStack;
import dev.xkmc.l2modularblock.impl.BlockEntityBlockMethodImpl;
import dev.xkmc.l2modularblock.mult.SetPlacedByBlockMethod;
import dev.xkmc.l2modularblock.mult.UseItemOnBlockMethod;
import dev.xkmc.l2modularblock.one.BlockEntityBlockMethod;
import dev.xkmc.l2modularblock.one.GetBlockItemBlockMethod;
import dev.xkmc.l2modularblock.one.SpecialDropBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DrawerBlock implements UseItemOnBlockMethod, GetBlockItemBlockMethod, SpecialDropBlockMethod, SetPlacedByBlockMethod {

	public static final DrawerBlock INSTANCE = new DrawerBlock();

	public static final BlockEntityBlockMethod<DrawerBlockEntity> BLOCK_ENTITY =
			new BlockEntityBlockMethodImpl<>(LBBlocks.TE_DRAWER, DrawerBlockEntity.class);

	@Override
	public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof DrawerBlockEntity chest) {
			if (stack.getItem() instanceof IBagTool tool) {
				return tool.clickDrawerBlock(stack, chest);
			}
			if (!stack.isEmpty() && chest.handler.isItemValid(0, stack)) {
				if (!level.isClientSide()) {
					stack = chest.handler.insertItem(0, stack, false);
					player.setItemInHand(hand, stack);
				} else {
					ContentTransfer.playDrawerSound(player);
				}
				return ItemInteractionResult.SUCCESS;
			} else if (stack.isEmpty()) {
				if (!level.isClientSide()) {
					stack = chest.handler.extractItem(0, chest.getItem().getMaxStackSize(), false);
					player.setItemInHand(hand, stack);
				} else {
					ContentTransfer.playDrawerSound(player);
				}
				return ItemInteractionResult.SUCCESS;
			}
			return ItemInteractionResult.FAIL;
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack stack) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof DrawerBlockEntity chest) {
			chest.handler.count = DrawerItem.getCount(stack);
			chest.handler.item = LBItems.DRAWER.get().getDrawerContent(stack);
			chest.handler.config = PickupConfig.get(stack);
			chest.handler.stacking = LBItems.DC_DRAWER_STACKING.getOrDefault(stack, 1);
		}
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof DrawerBlockEntity chest) {
			return buildStack(chest);
		}
		return LBItems.DRAWER.asStack();
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		BlockEntity blockentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockentity instanceof DrawerBlockEntity chest) {
			return List.of(buildStack(chest));
		}
		return List.of(LBItems.DRAWER.asStack());
	}

	private ItemStack buildStack(DrawerBlockEntity chest) {
		ItemStack stack = LBItems.DRAWER.asStack();
		LBItems.DC_DRAWER_STACK.set(stack, new DCStack(chest.handler.item));
		LBItems.DC_DRAWER_COUNT.set(stack, chest.handler.count);
		LBItems.DC_DRAWER_STACKING.set(stack, chest.handler.stacking);
		LBItems.DC_PICKUP.set(stack, chest.handler.config);
		return stack;
	}

}
