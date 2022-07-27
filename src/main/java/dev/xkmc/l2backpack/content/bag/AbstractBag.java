package dev.xkmc.l2backpack.content.bag;

import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractBag extends Item {

	public static final int SIZE = 64;

	public AbstractBag(Properties props) {
		super(props.stacksTo(1));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player != null && player.isShiftKeyDown()) {
			BlockPos pos = context.getClickedPos();
			BlockEntity target = context.getLevel().getBlockEntity(pos);
			if (target != null) {
				var capLazy = target.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
				if (capLazy.resolve().isPresent()) {
					var cap = capLazy.resolve().get();
					if (!context.getLevel().isClientSide()) {
						ItemStack stack = context.getItemInHand();
						NonNullList<ItemStack> list = NonNullList.withSize(SIZE, ItemStack.EMPTY);
						CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
						if (tag.contains("Items")) {
							ContainerHelper.loadAllItems(tag, list);
						}
						ContentTransfer.transfer(list, cap);
						ContainerHelper.saveAllItems(tag, list);
					}
					return InteractionResult.SUCCESS;
				}
				return InteractionResult.FAIL;
			}
		}
		return super.useOn(context);
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
			add(list, queue);
		}
		ContainerHelper.saveAllItems(tag, list);
		return InteractionResultHolder.success(stack);
	}

	public abstract boolean matches(ItemStack self, ItemStack stack);

	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
		list.add(LangData.IDS.BAG_SIZE.get(getSize(stack), SIZE));
		list.add(LangData.IDS.BAG_INFO.get());
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
		for (ItemStack stack : list) {
			if (!stack.isEmpty()) {
				player.getInventory().placeItemBackInInventory(stack.copy());
			}
		}
		list.clear();
	}

	private static void add(NonNullList<ItemStack> list, Queue<Holder<ItemStack>> toAdd) {
		for (int i = 0; i < SIZE; i++) {
			if (list.get(i).isEmpty()) {
				if (toAdd.isEmpty()) return;
				Holder<ItemStack> item = toAdd.poll();
				list.set(i, item.getter.get().copy());
				item.remove.run();
			}
		}
	}

	private record Holder<T>(Supplier<T> getter, Runnable remove) {

	}


}
