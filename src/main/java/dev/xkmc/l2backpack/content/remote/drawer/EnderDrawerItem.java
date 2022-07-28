package dev.xkmc.l2backpack.content.remote.drawer;

import dev.xkmc.l2backpack.content.common.BaseItemRenderer;
import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.remote.DrawerAccess;
import dev.xkmc.l2backpack.events.TooltipUpdateEvents;
import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.network.chat.Component;
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
import java.util.function.Consumer;

public class EnderDrawerItem extends BlockItem implements BaseDrawerItem {

	public static final String KEY_OWNER_ID = "owner_id";
	public static final String KEY_OWNER_NAME = "owner_name";

	public static final int MAX = 64;

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
			drawer.getOrCreateTag().putString(KEY_OWNER_NAME, player.getName().toString());
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (BaseDrawerItem.getItem(stack) == Items.AIR)
			return InteractionResultHolder.fail(stack);
		if (world.isClientSide())
			return InteractionResultHolder.success(stack);
		else refresh(stack, player);
		if (!player.isShiftKeyDown()) {
			ItemStack take = takeItem(stack, player);
			int c = take.getCount();
			player.getInventory().placeItemBackInInventory(take);
			ContentTransfer.onExtract(player, c);
		} else {
			DrawerAccess access = DrawerAccess.of(player, BaseDrawerItem.getItem(stack));
			int count = access.getCount();
			int max = MAX * access.item().getMaxStackSize();
			int ext = BaseDrawerItem.loadFromInventory(max, count, access.item(), player);
			count += ext;
			access.setCount(count);
			ContentTransfer.onCollect(player, ext);
		}
		return InteractionResultHolder.success(stack);
	}


	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!context.getLevel().isClientSide() && context.getPlayer() != null)
			refresh(context.getItemInHand(), context.getPlayer());
		if (!context.getItemInHand().getOrCreateTag().contains(KEY_OWNER_ID))
			return InteractionResult.FAIL;
		if (BaseDrawerItem.getItem(context.getItemInHand()) == Items.AIR)
			return InteractionResult.FAIL;
		if (context.getPlayer() != null && !context.getPlayer().isShiftKeyDown()) {
			return InteractionResult.PASS;
		}
		InteractionResult result = super.useOn(context);
		if (result == InteractionResult.FAIL) return InteractionResult.PASS;
		return result;
	}

	@Override
	public void insert(ItemStack drawer, ItemStack stack, Player player) {
		DrawerAccess access = DrawerAccess.of(player, BaseDrawerItem.getItem(drawer));
		int count = access.getCount();
		int take = Math.min(MAX * stack.getMaxStackSize() - count, stack.getCount());
		access.setCount(access.getCount() + take);
		stack.shrink(take);
	}

	@Override
	public ItemStack takeItem(ItemStack drawer, Player player) {
		DrawerAccess access = DrawerAccess.of(player, BaseDrawerItem.getItem(drawer));
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
		BaseDrawerItem.super.setItem(drawer, item, player);
		refresh(drawer, player);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		Item item = BaseDrawerItem.getItem(stack);
		if (item != Items.AIR) {
			int count = TooltipUpdateEvents.getCount(item);
			list.add(LangData.IDS.DRAWER_CONTENT.get(item.getDescription(), count < 0 ? "???" : count));
		}
		LangData.addInfo(list,
				LangData.Info.EXTRACT_DRAWER,
				LangData.Info.PLACE,
				LangData.Info.COLLECT_DRAWER,
				LangData.Info.DRAWER_USE,
				LangData.Info.ENDER_DRAWER_USE);
	}

	public String getDescriptionId() {
		return this.getOrCreateDescriptionId();
	}

}
