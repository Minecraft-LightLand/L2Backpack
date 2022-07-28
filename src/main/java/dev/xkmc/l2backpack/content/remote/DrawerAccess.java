package dev.xkmc.l2backpack.content.remote;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.UUID;

public record DrawerAccess(WorldStorage storage, UUID id, Item item) {

	public static DrawerAccess of(Player player, Item item) {
		return new DrawerAccess(WorldStorage.get((ServerLevel) player.getLevel()), player.getUUID(), item);
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
