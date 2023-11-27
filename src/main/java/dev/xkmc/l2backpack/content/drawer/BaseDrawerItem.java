package dev.xkmc.l2backpack.content.drawer;

import dev.xkmc.l2backpack.content.capability.PickupModeCap;
import dev.xkmc.l2backpack.content.capability.PickupBagItem;
import dev.xkmc.l2backpack.content.insert.OverlayInsertItem;
import dev.xkmc.l2backpack.init.advancement.BackpackTriggers;
import dev.xkmc.l2backpack.network.DrawerInteractToServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface BaseDrawerItem extends PickupBagItem, OverlayInsertItem {

	String KEY = "drawerItem", STACKING = "StackingFactor";

	int MAX_FACTOR = 8;

	static boolean canAccept(ItemStack drawer, ItemStack stack) {
		return !stack.hasTag() && !stack.isEmpty() && stack.getItem() == getItem(drawer);
	}

	static Item getItem(ItemStack drawer) {
		return Optional.ofNullable(drawer.getTag())
				.map(e -> e.contains(KEY) ? e.getString(KEY) : null)
				.map(e -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(e)))
				.orElse(Items.AIR);
	}

	static int loadFromInventory(int max, int count, Item item, Player player) {
		int ext = 0;
		for (int i = 0; i < 36; i++) {
			ItemStack inv_stack = player.getInventory().items.get(i);
			if (inv_stack.getItem() == item && !inv_stack.hasTag()) {
				int take = Math.min(max - count, inv_stack.getCount());
				count += take;
				ext += take;
				inv_stack.shrink(take);
				if (count == max) break;
			}
		}
		return ext;
	}

	static int getStacking(ItemStack drawer) {
		return getStacking(PickupModeCap.getConfig(drawer));
	}

	static int getStacking(@Nullable CompoundTag tag) {
		if (tag == null) return getStacking();
		int factor = tag.getInt(STACKING);
		if (factor < 1) factor = 1;
		return getStacking() * factor;
	}

	static int getStackingFactor(ItemStack drawer) {
		int factor = PickupModeCap.getConfig(drawer).getInt(STACKING);
		if (factor < 1) factor = 1;
		return factor;
	}

	static ItemStack setStackingFactor(ItemStack drawer, int factor) {
		PickupModeCap.getConfig(drawer).putInt(STACKING, factor);
		return drawer;
	}

	static int getStacking() {
		return 64;
	}

	void insert(ItemStack drawer, ItemStack stack, Player player);

	default void setItem(ItemStack drawer, Item item, Player player) {
		ResourceLocation rl = ForgeRegistries.ITEMS.getKey(item);
		assert rl != null;
		drawer.getOrCreateTag().putString(KEY, rl.toString());
	}

	default ItemStack takeItem(ItemStack drawer, ServerPlayer player) {
		ItemStack stack = takeItem(drawer, Integer.MAX_VALUE, player, false);
		if (!stack.isEmpty()) {
			BackpackTriggers.DRAWER.trigger(player, DrawerInteractToServer.Type.TAKE);
		}
		return stack;
	}

	ItemStack takeItem(ItemStack drawer, int max, Player player, boolean simulate);

	boolean canSetNewItem(ItemStack drawer);

	@Override
	default boolean clientInsert(ItemStack storage, ItemStack carried, int cid, Slot slot, boolean perform, int button) {
		if (carried.isEmpty()) return false;
		if (carried.hasTag()) return true;
		if (canSetNewItem(storage)) {
			if (perform)
				sendInsertPacket(cid, carried, slot);
			return true;
		}
		if (BaseDrawerItem.canAccept(storage, carried)) {
			if (perform)
				sendInsertPacket(cid, carried, slot);
			return true;
		}
		return false;
	}

	default boolean mayClientTake() {
		return true;
	}

	@Override
	default void attemptInsert(ItemStack storage, ItemStack carried, ServerPlayer player) {
		if (carried.isEmpty() || carried.hasTag()) return;
		if (canSetNewItem(storage)) {
			setItem(storage, carried.getItem(), player);
		}
		if (BaseDrawerItem.canAccept(storage, carried)) {
			insert(storage, carried, player);
			BackpackTriggers.DRAWER.trigger(player, DrawerInteractToServer.Type.INSERT);
		}
	}

}
