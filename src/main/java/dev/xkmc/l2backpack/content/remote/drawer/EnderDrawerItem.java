package dev.xkmc.l2backpack.content.remote.drawer;

import dev.xkmc.l2backpack.content.render.BaseItemRenderer;
import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.remote.common.DrawerAccess;
import dev.xkmc.l2backpack.events.TooltipUpdateEvents;
import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class EnderDrawerItem extends BlockItem implements BaseDrawerItem {

	public static final String KEY_OWNER_ID = "owner_id";
	public static final String KEY_OWNER_NAME = "owner_name";

	public static final int MAX = 64;

	public static Optional<UUID> getOwner(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag != null) {
			if (tag.contains(KEY_OWNER_ID)) {
				return Optional.of(tag.getUUID(KEY_OWNER_ID));
			}
		}
		return Optional.empty();
	}

	public EnderDrawerItem(Block block, Properties properties) {
		super(block, properties.stacksTo(1).fireResistant());
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(BaseItemRenderer.EXTENSIONS);
	}

	void refresh(ItemStack drawer, Player player) {
		if (!drawer.getOrCreateTag().contains(KEY_OWNER_ID)) {
			drawer.getOrCreateTag().putUUID(KEY_OWNER_ID, player.getUUID());
			drawer.getOrCreateTag().putString(KEY_OWNER_NAME, player.getName().getString());
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (BaseDrawerItem.getItem(stack) == Items.AIR)
			return InteractionResultHolder.fail(stack);
		if (world.isClientSide()) {
			ContentTransfer.playDrawerSound(player);
			return InteractionResultHolder.success(stack);
		} else refresh(stack, player);
		if (!player.isShiftKeyDown()) {
			ItemStack take = takeItem(stack, player);
			int c = take.getCount();
			player.getInventory().placeItemBackInInventory(take);
			ContentTransfer.onExtract(player, c, stack);
		} else {
			DrawerAccess access = DrawerAccess.of(world, stack);
			int count = access.getCount();
			int max = MAX * access.item().getMaxStackSize();
			int ext = BaseDrawerItem.loadFromInventory(max, count, access.item(), player);
			count += ext;
			access.setCount(count);
			ContentTransfer.onCollect(player, ext, stack);
		}
		return InteractionResultHolder.success(stack);
	}


	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!context.getLevel().isClientSide() && context.getPlayer() != null)
			refresh(context.getItemInHand(), context.getPlayer());
		if (!context.getItemInHand().getOrCreateTag().contains(KEY_OWNER_ID))
			return InteractionResult.FAIL;
		if (BaseDrawerItem.getItem(context.getItemInHand()) == Items.AIR) {
			if (!context.getLevel().isClientSide()) {
				if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
					serverPlayer.sendSystemMessage(LangData.IDS.NO_ITEM.get().withStyle(ChatFormatting.RED), true);
				}
			}
			return InteractionResult.FAIL;
		}
		if (context.getPlayer() != null && !context.getPlayer().isShiftKeyDown()) {
			return InteractionResult.PASS;
		}
		InteractionResult result = super.useOn(context);
		if (result == InteractionResult.FAIL) return InteractionResult.PASS;
		return result;
	}

	@Override
	public void insert(ItemStack drawer, ItemStack stack, Player player) {
		refresh(drawer, player);
		DrawerAccess access = DrawerAccess.of(player.getLevel(), drawer);
		int count = access.getCount();
		int take = Math.min(MAX * stack.getMaxStackSize() - count, stack.getCount());
		access.setCount(access.getCount() + take);
		stack.shrink(take);
	}

	@Override
	public ItemStack takeItem(ItemStack drawer, Player player) {
		refresh(drawer, player);
		DrawerAccess access = DrawerAccess.of(player.getLevel(), drawer);
		Item item = BaseDrawerItem.getItem(drawer);
		int take = Math.min(access.getCount(), item.getMaxStackSize());
		access.setCount(access.getCount() - take);
		return new ItemStack(item, take);
	}

	@Override
	public boolean canSetNewItem(ItemStack drawer) {
		return BaseDrawerItem.getItem(drawer) == Items.AIR;
	}

	@Override
	public void setItem(ItemStack drawer, Item item, Player player) {
		refresh(drawer, player);
		BaseDrawerItem.super.setItem(drawer, item, player);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		Item item = BaseDrawerItem.getItem(stack);
		if (item != Items.AIR) {
			int count = TooltipUpdateEvents.getCount(stack.getOrCreateTag().getUUID(KEY_OWNER_ID), item);
			list.add(LangData.IDS.DRAWER_CONTENT.get(item.getDescription(), count < 0 ? "???" : count));
		}
		CompoundTag tag = stack.getTag();
		if (tag != null) {
			if (tag.contains(KEY_OWNER_NAME)) {
				String name = tag.getString(KEY_OWNER_NAME);
				list.add(LangData.IDS.STORAGE_OWNER.get(name));
			}
		}
		LangData.addInfo(list,
				LangData.Info.DRAWER_USE,
				LangData.Info.EXTRACT_DRAWER,
				LangData.Info.PLACE,
				LangData.Info.COLLECT_DRAWER,
				LangData.Info.ENDER_DRAWER,
				LangData.Info.ENDER_DRAWER_USE);
	}

	public String getDescriptionId() {
		return this.getOrCreateDescriptionId();
	}

}
