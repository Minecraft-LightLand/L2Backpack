package dev.xkmc.l2backpack.content.bag;

import dev.xkmc.l2backpack.content.capability.PickupBagItem;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.click.DoubleClickItem;
import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.content.common.InvTooltip;
import dev.xkmc.l2backpack.content.common.TooltipInvItem;
import dev.xkmc.l2backpack.content.insert.CapInsertItem;
import dev.xkmc.l2backpack.init.data.LBLang;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public abstract class AbstractBag extends Item
		implements ContentTransfer.Quad, PickupBagItem, CapInsertItem, TooltipInvItem, DoubleClickItem {

	public static final int SIZE = 64;

	private static final List<AbstractBag> LIST = new ArrayList<>();

	private static synchronized void add(AbstractBag bag) {
		LIST.add(bag);
	}

	public static synchronized List<AbstractBag> getAllBags() {
		return LIST;
	}

	public static boolean isFilled(ItemStack bag) {
		return ((AbstractBag) bag.getItem()).getSize(bag) > 0;
	}

	public AbstractBag(Properties props) {
		super(props.stacksTo(1));
		add(this);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		return ContentTransfer.blockInteract(context, this);
	}

	public NonNullList<ItemStack> getContent(ItemStack stack) {
		NonNullList<ItemStack> list = NonNullList.withSize(SIZE, ItemStack.EMPTY);
		var cont = stack.get(LBItems.BAG_CONTENT);
		if (cont != null) cont.copyInto(list);
		return list;
	}

	public void setContent(ItemStack stack, NonNullList<ItemStack> list) {
		stack.set(LBItems.BAG_CONTENT, ItemContainerContents.fromItems(list));
	}

	@Override
	public void click(Player player, ItemStack stack, boolean client, boolean shift, boolean right, @Nullable IItemHandler target) {
		if (!client && shift && right && target != null) {
			NonNullList<ItemStack> list = getContent(stack);
			int pre = 0;
			for (ItemStack inv : list) pre += inv.getCount();
			ContentTransfer.transfer(list, target);
			int post = 0;
			for (ItemStack inv : list) post += inv.getCount();
			setContent(stack, list);
			ContentTransfer.onDump(player, pre - post, stack);
		} else if (client && shift && right && target != null)
			ContentTransfer.playSound(player);
		if (!client && shift && !right && target != null) {
			NonNullList<ItemStack> list = getContent(stack);
			int count = ContentTransfer.loadFrom(list, target, player, this::isValidContent);
			setContent(stack, list);
			ContentTransfer.onLoad(player, count, stack);
		} else if (client && shift && !right && target != null)
			ContentTransfer.playSound(player);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (world.isClientSide()) {
			ContentTransfer.playSound(player);
			return InteractionResultHolder.success(stack);
		}
		NonNullList<ItemStack> list = getContent(stack);
		if (player.isShiftKeyDown()) {
			throwOut(list, player, stack);
		} else {
			add(list, player, stack);
		}
		setContent(stack, list);
		return InteractionResultHolder.success(stack);
	}

	@Override
	public int getRowSize() {
		return 8;
	}

	@Override
	public int getInvSize(ItemStack stack) {
		return SIZE;
	}

	@Override
	public List<ItemStack> getInvItems(ItemStack stack, Player player) {
		return getContent(stack);
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return InvTooltip.get(this, stack);
	}

	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		if (flag.hasAltDown()) return;
		list.add(LBLang.IDS.BAG_SIZE.get(getSize(stack), SIZE));
		PickupConfig.addText(stack, list);
		LBLang.addInfo(flag, list,
				LBLang.Info.COLLECT_BAG,
				LBLang.Info.LOAD,
				LBLang.Info.EXTRACT_BAG);
		if (flag.hasShiftDown()) return;
		list.add(LBLang.Info.ALT_CONTENT.get().withStyle(ChatFormatting.GRAY));
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}

	public int getSize(ItemStack stack) {
		NonNullList<ItemStack> list = getContent(stack);
		int ans = 0;
		for (ItemStack is : list) {
			if (!is.isEmpty()) {
				ans++;
			}
		}
		return ans;
	}

	@Override
	public boolean mayClientTake() {
		return true;
	}

	@Override
	public ItemStack takeItem(ItemStack storage, ServerPlayer player) {
		var list = getContent(storage);
		for (int i = 0; i < SIZE; i++) {
			if (!list.get(i).isEmpty()) {
				ItemStack ans = list.get(i).copy();
				list.set(i, ItemStack.EMPTY);
				setContent(storage, list);
				return ans;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public int remainingSpace(ItemStack stack) {
		return SIZE - getSize(stack);
	}

	@Override
	public boolean canAbsorb(Slot src, ItemStack stack) {
		return isValidContent(src.getItem());
	}

	@Override
	public void mergeStack(ItemStack stack, ItemStack taken) {
		var list = getContent(stack);
		for (int i = 0; i < SIZE; i++) {
			if (list.get(i).isEmpty()) {
				list.set(i, taken);
				break;
			}
		}
		setContent(stack, list);
	}

	private void throwOut(NonNullList<ItemStack> list, Player player, ItemStack bag) {
		int count = 0;
		int stackCount = 0;
		for (int i = 0; i < SIZE; i++) {
			ItemStack stack = list.get(i);
			if (!stack.isEmpty()) {
				count += stack.getCount();
				stackCount++;
				BagUtils.placeItemBackInInventory(player.getInventory(), stack.copy());
				list.set(i, ItemStack.EMPTY);
				if (stackCount >= 16) break;
			}
		}
		ContentTransfer.onExtract(player, count, bag);
	}

	private void add(NonNullList<ItemStack> list, Player player, ItemStack bag) {
		int count = 0;
		int slot = 0;
		var inv = player.getInventory();
		for (int i = 9; i < 36; i++) {
			ItemStack stack = inv.items.get(i);
			if (isValidContent(stack)) {
				while (slot < SIZE && !list.get(slot).isEmpty()) slot++;
				if (slot >= SIZE) break;
				list.set(slot, stack);
				count += stack.getCount();
				inv.items.set(i, ItemStack.EMPTY);
				slot++;
			}
		}
		ContentTransfer.onCollect(player, count, bag);
	}

}
