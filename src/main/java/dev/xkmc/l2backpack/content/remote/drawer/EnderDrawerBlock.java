package dev.xkmc.l2backpack.content.remote.drawer;

import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.remote.common.DrawerAccess;
import dev.xkmc.l2backpack.init.registrate.BackpackBlocks;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import dev.xkmc.l2modularblock.mult.OnClickBlockMethod;
import dev.xkmc.l2modularblock.mult.SetPlacedByBlockMethod;
import dev.xkmc.l2modularblock.one.BlockEntityBlockMethod;
import dev.xkmc.l2modularblock.one.GetBlockItemBlockMethod;
import dev.xkmc.l2modularblock.one.SpecialDropBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class EnderDrawerBlock implements OnClickBlockMethod, GetBlockItemBlockMethod, SpecialDropBlockMethod, SetPlacedByBlockMethod {

	public static final EnderDrawerBlock INSTANCE = new EnderDrawerBlock();

	public static final BlockEntityBlockMethod<EnderDrawerBlockEntity> BLOK_ENTITY =
			new EnderDrawerAnalogBlockEntity<>(BackpackBlocks.TE_ENDER_DRAWER, EnderDrawerBlockEntity.class);

	@Override
	public InteractionResult onClick(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		ItemStack stack = player.getItemInHand(hand);
		if (blockentity instanceof EnderDrawerBlockEntity chest) {
			if (!stack.isEmpty() && !stack.hasTag() && stack.getItem() == chest.item) {
				if (!level.isClientSide()) {
					stack = new EnderDawerItemHandler(chest.getAccess(), false).insertItem(0, stack, false);
					player.setItemInHand(hand, stack);
				} else {
					ContentTransfer.playDrawerSound(player);
				}
				return InteractionResult.SUCCESS;
			} else if (stack.isEmpty()) {
				if (!level.isClientSide()) {
					DrawerAccess access = chest.getAccess();
					stack = new EnderDawerItemHandler(access, false).extractItem(0, access.item().getMaxStackSize(), false);
					player.setItemInHand(hand, stack);
				} else {
					ContentTransfer.playDrawerSound(player);
				}
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.FAIL;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack stack) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		UUID id = stack.getOrCreateTag().getUUID(EnderDrawerItem.KEY_OWNER_ID);
		String name = stack.getOrCreateTag().getString(EnderDrawerItem.KEY_OWNER_NAME);
		if (blockentity instanceof EnderDrawerBlockEntity chest) {
			chest.owner_id = id;
			chest.owner_name = name;
			chest.item = BaseDrawerItem.getItem(stack);
			chest.addToListener();
		}
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof EnderDrawerBlockEntity chest) {
			return buildStack(chest);
		}
		return BackpackItems.ENDER_DRAWER.asStack();
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		BlockEntity blockentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockentity instanceof EnderDrawerBlockEntity chest) {
			return List.of(buildStack(chest));
		}
		return List.of(BackpackItems.ENDER_DRAWER.asStack());
	}

	private ItemStack buildStack(EnderDrawerBlockEntity chest) {
		ItemStack stack = BackpackItems.ENDER_DRAWER.asStack();
		if (chest.owner_id != null) {
			stack.getOrCreateTag().putUUID(EnderDrawerItem.KEY_OWNER_ID, chest.owner_id);
			stack.getOrCreateTag().putString(EnderDrawerItem.KEY_OWNER_NAME, chest.owner_name);
			ResourceLocation rl = ForgeRegistries.ITEMS.getKey(chest.item);
			assert rl != null;
			stack.getOrCreateTag().putString(BaseDrawerItem.KEY, rl.toString());
		}
		return stack;
	}

}
