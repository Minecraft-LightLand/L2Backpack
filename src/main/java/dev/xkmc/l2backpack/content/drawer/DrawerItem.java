package dev.xkmc.l2backpack.content.drawer;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.click.DoubleClickItem;
import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.content.insert.OverlayInsertItem;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.LBLang;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2core.util.DCStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DrawerItem extends BlockItem implements BaseDrawerItem, ContentTransfer.Quad, DoubleClickItem {

	public static int getCount(ItemStack drawer) {
		return LBItems.DC_DRAWER_COUNT.getOrDefault(drawer, 0);
	}

	public static void setCount(ItemStack drawer, int count) {
		LBItems.DC_DRAWER_COUNT.set(drawer, count);
	}

	public DrawerItem(Block block, Properties properties) {
		super(block, properties.stacksTo(1).fireResistant());
	}

	@Override
	public ItemStack getDrawerContent(ItemStack drawer) {
		ItemStack stack = LBItems.DC_DRAWER_STACK.getOrDefault(drawer, new DCStack(ItemStack.EMPTY)).stack();
		int count = getCount(drawer);
		return count == 0 ? ItemStack.EMPTY : stack;
	}

	@Override
	public void setItem(ItemStack drawer, ItemStack item, Player player) {
		if (item.isEmpty()) {
			drawer.remove(LBItems.DC_DRAWER_STACK);
			drawer.remove(LBItems.DC_DRAWER_COUNT);
		} else {
			LBItems.DC_DRAWER_STACK.set(drawer, new DCStack(item.copyWithCount(1)));
		}
	}

	@Override
	public boolean canAccept(ItemStack drawer, ItemStack stack) {
		if (!stack.getItem().canFitInsideContainerItems()) return false;
		if (stack.getItem() instanceof OverlayInsertItem) return false;
		ItemStack content = getDrawerContent(drawer);
		return content.isEmpty() || ItemStack.isSameItemSameComponents(content, stack);
	}

	@Override
	public int getStacking(ItemStack drawer) {
		return BaseDrawerItem.super.getStacking(drawer) * LBItems.DC_DRAWER_STACKING.getOrDefault(drawer, 1);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack drawer = player.getItemInHand(hand);
		if (world.isClientSide()) {
			ContentTransfer.playDrawerSound(player);
			return InteractionResultHolder.success(drawer);
		}
		if (!player.isShiftKeyDown()) {
			ItemStack item = getDrawerContent(drawer);
			int count = getCount(drawer);
			int max = Math.min(item.getMaxStackSize(), count);
			player.getInventory().placeItemBackInInventory(item.copyWithCount(max));
			setCount(drawer, count - max);
			ContentTransfer.onExtract(player, max, drawer);
		} else {
			ItemStack item = getDrawerContent(drawer);
			int count = getCount(drawer);
			int max = getStacking(drawer, item);
			boolean perform = !canSetNewItem(drawer);
			if (!perform) {
				item = ContentTransfer.filterMaxItem(new InvWrapper(player.getInventory()));
				if (!item.isEmpty()) {
					perform = true;
					setItem(drawer, item, player);
				}
			}
			if (perform) {
				int ext = BaseDrawerItem.loadFromInventory(max, count, item, player);
				count += ext;
				setCount(drawer, count);
				ContentTransfer.onCollect(player, ext, drawer);
			}
		}
		return InteractionResultHolder.success(drawer);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		InteractionResult result = ContentTransfer.blockInteract(context, this);
		if (result == InteractionResult.PASS && context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
			result = super.useOn(context);
			if (result == InteractionResult.FAIL) {
				result = InteractionResult.PASS;
			}
		}
		return result;
	}

	@Override
	public void click(Player player, ItemStack stack, boolean client, boolean shift, boolean right, @Nullable IItemHandler target) {
		if (!client && shift && right && target != null) {
			ItemStack item = getDrawerContent(stack);
			int count = getCount(stack);
			int remain = ContentTransfer.transfer(item, count, target);
			ContentTransfer.onDump(player, count - remain, stack);
			setCount(stack, remain);
		} else if (client && shift && right && target != null)
			ContentTransfer.playDrawerSound(player);
		if (!client && shift && !right && target != null) {
			ItemStack item = getDrawerContent(stack);
			boolean perform = !canSetNewItem(stack);
			if (!perform) {
				item = ContentTransfer.filterMaxItem(target);
				if (!item.isEmpty()) {
					setItem(stack, item, player);
					perform = true;
				}
			}
			if (perform) {
				int count = getCount(stack);
				int max = getStacking(stack, item);
				int remain = ContentTransfer.loadFrom(item, max - count, target);
				ContentTransfer.onLoad(player, remain, stack);
				setCount(stack, count + remain);
			}
		} else if (client && shift && !right && target != null)
			ContentTransfer.playDrawerSound(player);
	}

	@Override
	public void insert(ItemStack drawer, ItemStack stack, @Nullable Player player) {
		int count = getCount(drawer);
		int allow = Math.min(getStacking(drawer, stack) - count, stack.getCount());
		setCount(drawer, count + allow);
		stack.shrink(allow);
	}

	@Override
	public ItemStack takeItem(ItemStack drawer, int max, @Nullable Player player, boolean simulate) {
		if (canSetNewItem(drawer)) return ItemStack.EMPTY;
		ItemStack item = getDrawerContent(drawer);
		int count = getCount(drawer);
		int take = Math.min(count, Math.min(max, item.getMaxStackSize()));
		if (!simulate)
			setCount(drawer, count - take);
		return item.copyWithCount(take);
	}

	@Override
	public boolean canSetNewItem(ItemStack drawer) {
		return getDrawerContent(drawer).isEmpty();
	}

	@Override
	public void appendHoverText(ItemStack drawer, TooltipContext context, List<Component> list, TooltipFlag flag) {
		ItemStack content = getDrawerContent(drawer);
		int count = getCount(drawer);
		if (!canSetNewItem(drawer)) {
			list.add(LBLang.IDS.DRAWER_CONTENT.get(content.getHoverName(), count));
		}
		list.add(LBLang.IDS.BACKPACK_SLOT.get(LBItems.DC_DRAWER_STACKING.getOrDefault(drawer, 1), MAX_FACTOR)
				.withStyle(ChatFormatting.GRAY));
		PickupConfig.addText(drawer, list);
		LBLang.addInfo(flag, list,
				LBLang.Info.DRAWER_USE,
				LBLang.Info.LOAD,
				LBLang.Info.PLACE,
				LBLang.Info.EXTRACT_DRAWER,
				LBLang.Info.COLLECT_DRAWER,
				LBLang.Info.PICKUP);
	}

	public String getDescriptionId() {
		return this.getOrCreateDescriptionId();
	}

	public DrawerInvWrapper getCaps(ItemStack stack, @Nullable Void ignored) {
		var access = new DrawerInvAccess(stack, this);
		return new DrawerInvWrapper(stack, trace -> access);
	}

	private static final ResourceLocation BG = L2Backpack.loc("textures/block/drawer/drawer_side.png");

	@Override
	public ResourceLocation backgroundLoc() {
		return BG;
	}

	@Override
	public int remainingSpace(ItemStack drawer) {
		if (canSetNewItem(drawer)) return 0;
		int count = getCount(drawer);
		return getStacking(drawer, getDrawerContent(drawer)) - count;
	}

	@Override
	public boolean canAbsorb(Slot src, ItemStack stack) {
		if (canSetNewItem(stack)) return false;
		return canAccept(stack, src.getItem());
	}

	@Override
	public void mergeStack(ItemStack drawer, ItemStack stack) {
		int count = getCount(drawer);
		int allow = Math.min(getStacking(drawer, stack) - count, stack.getCount());
		setCount(drawer, count + allow);
		stack.shrink(allow);
	}

}
