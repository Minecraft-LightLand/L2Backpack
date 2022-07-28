package dev.xkmc.l2backpack.content.remote;

import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.UUID;

public record DrawerAccess(WorldStorage storage, UUID id, Item item) {

	public static DrawerAccess of(Level level, ItemStack drawer) {
		UUID id = drawer.getOrCreateTag().getUUID(EnderDrawerItem.KEY_OWNER_ID);
		Item item = BaseDrawerItem.getItem(drawer);
		return new DrawerAccess(WorldStorage.get((ServerLevel) level), id, item);
	}

	private HashMap<Item, Integer> getMap() {
		return storage.drawer.computeIfAbsent(id.toString(), k -> new HashMap<>());
	}

	public int getCount() {
		return getMap().computeIfAbsent(item, k -> 0);
	}

	public void setCount(int count) {
		getMap().put(item, count);
	}

}
