package dev.xkmc.l2backpack.content.arrowbag;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.registrate.BackpackMenu;
import dev.xkmc.l2library.base.menu.BaseContainerMenu;
import dev.xkmc.l2library.base.menu.SpriteManager;
import dev.xkmc.l2library.util.annotation.ServerOnly;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class ArrowBagContainer extends BaseContainerMenu<ArrowBagContainer> {

	public static final SpriteManager MANAGERS = new SpriteManager(L2Backpack.MODID, "backpack_1");

	public static ArrowBagContainer fromNetwork(MenuType<ArrowBagContainer> type, int windowId, Inventory inv, FriendlyByteBuf buf) {
		int slot = buf.readInt();
		UUID id = buf.readUUID();
		return new ArrowBagContainer(windowId, inv, slot, id);
	}

	protected final Player player;
	protected final int item_slot;
	protected final UUID uuid;

	public ArrowBagContainer(int windowId, Inventory inventory, int hand, UUID uuid) {
		super(BackpackMenu.MT_ARROW.get(), windowId, inventory, MANAGERS, menu -> new BaseContainer<>(9, menu), false);
		this.player = inventory.player;
		this.item_slot = hand;
		this.uuid = uuid;
		this.addSlot("grid", stack -> stack.getItem() instanceof ArrowItem);
		if (!this.player.level.isClientSide()) {
			ItemStack stack = getStack();
			if (!stack.isEmpty()) {
				ListTag tag = ArrowBag.getListTag(stack);
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
		}
		super.removed(player);
	}

	private void save() {
		ItemStack stack = getStack();
		if (!stack.isEmpty()) {
			ListTag list = new ListTag();
			for (int i = 0; i < this.container.getContainerSize(); i++) {
				list.add(i, this.container.getItem(i).save(new CompoundTag()));
			}
			ArrowBag.setListTag(stack, list);
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
		ItemStack stack = player.getInventory().getItem(item_slot);
		CompoundTag tag = stack.getTag();
		if (tag == null) return ItemStack.EMPTY;
		if (!tag.contains("container_id")) return ItemStack.EMPTY;
		if (!tag.getUUID("container_id").equals(uuid)) return ItemStack.EMPTY;
		stack_cache = stack;
		return stack;
	}


}
