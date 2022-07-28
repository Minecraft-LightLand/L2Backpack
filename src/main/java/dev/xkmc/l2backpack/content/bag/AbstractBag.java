package dev.xkmc.l2backpack.content.bag;

import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractBag extends Item implements ContentTransfer.Quad {

	public static final int SIZE = 64;

	public AbstractBag(Properties props) {
		super(props.stacksTo(1));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		return ContentTransfer.blockInteract(context, this);
	}

	@Override
	public void click(Player player, ItemStack stack, boolean client, boolean shift, boolean right, @org.jetbrains.annotations.Nullable IItemHandler target) {
		if (!client && shift && right && target != null) {
			NonNullList<ItemStack> list = NonNullList.withSize(SIZE, ItemStack.EMPTY);
			CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
			if (tag.contains("Items")) ContainerHelper.loadAllItems(tag, list);
			int pre = 0;
			for (ItemStack inv : list) pre += inv.getCount();
			ContentTransfer.transfer(list, target);
			int post = 0;
			for (ItemStack inv : list) post += inv.getCount();
			ContainerHelper.saveAllItems(tag, list);
			ContentTransfer.onDump(player, pre - post);
		}
		if (!client && shift && !right && target != null) {
			NonNullList<ItemStack> list = NonNullList.withSize(SIZE, ItemStack.EMPTY);
			CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
			if (tag.contains("Items")) ContainerHelper.loadAllItems(tag, list);
			int count = ContentTransfer.loadFrom(list, target, player, e -> matches(stack, e));
			ContainerHelper.saveAllItems(tag, list);
			ContentTransfer.onLoad(player, count);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (world.isClientSide())
			return InteractionResultHolder.success(stack);
		NonNullList<ItemStack> list = NonNullList.withSize(SIZE, ItemStack.EMPTY);
		CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
		if (tag.contains("Items")) {
			ContainerHelper.loadAllItems(tag, list);
		}
		if (player.isShiftKeyDown()) {
			throwOut(list, player);
		} else {
			Queue<Holder<ItemStack>> queue = new ArrayDeque<>();
			player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
					.resolve().ifPresent(e -> {
						for (int i = 9; i < 36; i++) {
							ItemStack inv_stack = player.getInventory().items.get(i);
							if (matches(stack, inv_stack)) {
								int finalI = i;
								queue.add(new Holder<>(
										() -> e.getStackInSlot(finalI),
										() -> e.extractItem(finalI, 1, false)));
							}
						}
					});
			int moved = add(list, queue);
			ContentTransfer.onCollect(player, moved);
		}
		ContainerHelper.saveAllItems(tag, list);
		return InteractionResultHolder.success(stack);
	}

	public abstract boolean matches(ItemStack self, ItemStack stack);

	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
		list.add(LangData.IDS.BAG_SIZE.get(getSize(stack), SIZE));
		LangData.addInfo(list,
				LangData.Info.COLLECT_BAG,
				LangData.Info.DUMP,
				LangData.Info.LOAD,
				LangData.Info.EXTRACT_BAG);
	}

	public boolean isBarVisible(ItemStack stack) {
		return getSize(stack) < SIZE;
	}

	public int getBarWidth(ItemStack stack) {
		return (int) Math.ceil(getSize(stack) * 13f / SIZE);
	}

	public int getBarColor(ItemStack stack) {
		return 0xFFFFFF;
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}

	private int getSize(ItemStack stack) {
		NonNullList<ItemStack> list = NonNullList.withSize(SIZE, ItemStack.EMPTY);
		CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
		if (tag.contains("Items")) {
			ContainerHelper.loadAllItems(tag, list);
		}
		int ans = 0;
		for (ItemStack is : list) {
			if (!is.isEmpty()) {
				ans++;
			}
		}
		return ans;
	}

	private void throwOut(NonNullList<ItemStack> list, Player player) {
		int count = 0;
		for (ItemStack stack : list) {
			if (!stack.isEmpty()) {
				count += stack.getCount();
				player.getInventory().placeItemBackInInventory(stack.copy());
			}
		}
		ContentTransfer.onExtract(player, count);
		list.clear();
	}

	private static int add(NonNullList<ItemStack> list, Queue<Holder<ItemStack>> toAdd) {
		int count = 0;
		for (int i = 0; i < SIZE; i++) {
			if (list.get(i).isEmpty()) {
				if (toAdd.isEmpty()) return count;
				Holder<ItemStack> item = toAdd.poll();
				list.set(i, item.getter.get().copy());
				item.remove.run();
				count++;
			}
		}
		return count;
	}

	private record Holder<T>(Supplier<T> getter, Runnable remove) {

	}


}
