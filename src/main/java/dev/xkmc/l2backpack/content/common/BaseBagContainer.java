package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.content.restore.ScreenTracker;
import dev.xkmc.l2library.base.menu.SpriteManager;
import dev.xkmc.l2library.util.annotation.ServerOnly;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Predicate;

public class BaseBagContainer<T extends BaseBagContainer<T>> extends BaseOpenableContainer<T> {

	protected final PlayerSlot item_slot;
	protected final UUID uuid;

	public BaseBagContainer(MenuType<T> type, int windowId, Inventory inventory, SpriteManager manager,
							PlayerSlot hand, UUID uuid, int row, Predicate<ItemStack> pred, @Nullable Component title) {
		super(type, windowId, inventory, manager, menu -> new BaseContainer<>(row * 9, menu), title);
		this.item_slot = hand;
		this.uuid = uuid;
		this.addSlot("grid", pred);
		if (!this.player.level.isClientSide()) {
			ItemStack stack = getStack();
			if (!stack.isEmpty()) {
				ListTag tag = BaseBagItem.getListTag(stack);
				for (int i = 0; i < tag.size(); i++) {
					this.container.setItem(i, ItemStack.of((CompoundTag) tag.get(i)));
				}
			}
		}
	}

	@Override
	public void slotsChanged(Container cont) {
		save();
	}

	@Override
	public void removed(Player player) {
		if (!player.level.isClientSide) {
			save();
			ScreenTracker.onServerClose(player, containerId);
		}
		super.removed(player);
	}

	private void save() {
		if (player.getLevel().isClientSide()) return;
		ItemStack stack = getStack();
		if (!stack.isEmpty()) {
			ListTag list = new ListTag();
			for (int i = 0; i < this.container.getContainerSize(); i++) {
				list.add(i, this.container.getItem(i).save(new CompoundTag()));
			}
			BaseBagItem.setListTag(stack, list);
		}
	}

	private ItemStack stack_cache = ItemStack.EMPTY;

	@ServerOnly
	@Override
	public boolean stillValid(Player player) {
		return !getStackRaw().isEmpty();
	}

	public ItemStack getStack() {
		ItemStack stack = getStackRaw();
		if (stack.isEmpty()) return stack_cache;
		return stack;
	}

	private ItemStack getStackRaw() {
		ItemStack stack = item_slot.getItem(player);
		CompoundTag tag = stack.getTag();
		if (tag == null) return ItemStack.EMPTY;
		if (!tag.contains("container_id")) return ItemStack.EMPTY;
		if (!tag.getUUID("container_id").equals(uuid)) return ItemStack.EMPTY;
		stack_cache = stack;
		return stack;
	}


}
