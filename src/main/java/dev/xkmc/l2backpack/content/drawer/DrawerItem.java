package dev.xkmc.l2backpack.content.drawer;

import dev.xkmc.l2backpack.content.common.BaseItemRenderer;
import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class DrawerItem extends Item implements BaseDrawerItem, ContentTransfer.Quad {

	private static final String COUNT = "drawerCount";
	private static final int MAX = 64;

	public static int getCount(ItemStack drawer) {
		return Optional.ofNullable(drawer.getTag()).map(e -> e.getInt(COUNT)).orElse(0);
	}

	public static void setCount(ItemStack drawer, int count) {
		drawer.getOrCreateTag().putInt(COUNT, count);
	}

	public DrawerItem(Properties properties) {
		super(properties.stacksTo(1).fireResistant());
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(BaseItemRenderer.EXTENSIONS);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (world.isClientSide()) {
			ContentTransfer.playDrawerSound(player);
			return InteractionResultHolder.success(stack);
		}
		if (!player.isShiftKeyDown()) {
			Item item = BaseDrawerItem.getItem(stack);
			int count = getCount(stack);
			int max = Math.min(item.getMaxStackSize(), count);
			player.getInventory().placeItemBackInInventory(new ItemStack(item, max));
			setCount(stack, count - max);
			ContentTransfer.onExtract(player, max);
		} else {
			Item item = BaseDrawerItem.getItem(stack);
			int count = getCount(stack);
			int max = item.getMaxStackSize() * MAX;
			boolean perform = !canSetNewItem(stack);
			if (!perform) {
				item = ContentTransfer.filterMaxItem(new InvWrapper(player.getInventory()));
				if (item != Items.AIR) {
					perform = true;
					setItem(stack, item, player);
				}
			}
			if (perform) {
				int ext = BaseDrawerItem.loadFromInventory(max, count, item, player);
				count += ext;
				setCount(stack, count);
				ContentTransfer.onCollect(player, ext);
			}
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		return ContentTransfer.blockInteract(context, this);
	}

	@Override
	public void click(Player player, ItemStack stack, boolean client, boolean shift, boolean right, @Nullable IItemHandler target) {
		if (!client && shift && right && target != null) {
			Item item = BaseDrawerItem.getItem(stack);
			int count = getCount(stack);
			int remain = ContentTransfer.transfer(item, count, target);
			ContentTransfer.onDump(player, count - remain);
			setCount(stack, remain);
		} else if (client && shift && right && target != null)
			ContentTransfer.playDrawerSound(player);
		if (!client && shift && !right && target != null) {
			Item item = BaseDrawerItem.getItem(stack);
			boolean perform = !canSetNewItem(stack);
			if (!perform) {
				item = ContentTransfer.filterMaxItem(target);
				if (item != Items.AIR) {
					setItem(stack, item, player);
					perform = true;
				}
			}
			if (perform) {
				int count = getCount(stack);
				int max = MAX * item.getMaxStackSize();
				int remain = ContentTransfer.loadFrom(item, max - count, target);
				ContentTransfer.onLoad(player, remain);
				setCount(stack, count + remain);
			}
		} else if (client && shift && !right && target != null)
			ContentTransfer.playDrawerSound(player);
	}

	@Override
	public void insert(ItemStack drawer, ItemStack stack, Player player) {
		int count = getCount(drawer);
		int allow = Math.min(MAX * stack.getMaxStackSize() - count, stack.getCount());
		setCount(drawer, count + allow);
		stack.shrink(allow);
	}

	@Override
	public ItemStack takeItem(ItemStack drawer, Player player) {
		if (canSetNewItem(drawer)) return ItemStack.EMPTY;
		Item item = BaseDrawerItem.getItem(drawer);
		int count = getCount(drawer);
		int take = Math.min(count, item.getMaxStackSize());
		setCount(drawer, count - take);
		return new ItemStack(item, take);
	}

	@Override
	public boolean canSetNewItem(ItemStack drawer) {
		return BaseDrawerItem.getItem(drawer) == Items.AIR || getCount(drawer) == 0;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		Item item = BaseDrawerItem.getItem(stack);
		int count = getCount(stack);
		if (!canSetNewItem(stack)) {
			list.add(LangData.IDS.DRAWER_CONTENT.get(item.getDescription(), count));
		}
		LangData.addInfo(list,
				LangData.Info.DRAWER_USE,
				LangData.Info.DUMP,
				LangData.Info.LOAD,
				LangData.Info.EXTRACT_DRAWER,
				LangData.Info.COLLECT_DRAWER);
	}

}
