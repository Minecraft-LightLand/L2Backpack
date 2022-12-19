package dev.xkmc.l2backpack.content.remote.common;

import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerBlockEntity;
import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerItem;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;

public final class DrawerAccess {

	public static DrawerAccess of(Level level, ItemStack drawer) {
		UUID id = drawer.getOrCreateTag().getUUID(EnderDrawerItem.KEY_OWNER_ID);
		Item item = BaseDrawerItem.getItem(drawer);
		return of(level, id, item);
	}


	public static DrawerAccess of(Level level, UUID id, Item item) {
		return WorldStorage.get((ServerLevel) level).getOrCreateDrawer(id, item);
	}

	private final WorldStorage storage;
	private final UUID id;
	private final Item item;

	public final List<EnderDrawerBlockEntity> listener = new ArrayList<>();

	DrawerAccess(WorldStorage storage, UUID id, Item item) {
		this.storage = storage;
		this.id = id;
		this.item = item;
	}

	private HashMap<Item, Integer> getMap() {
		return storage.drawer.computeIfAbsent(id.toString(), k -> new HashMap<>());
	}

	public int getCount() {
		return getMap().computeIfAbsent(item, k -> 0);
	}

	public void setCount(int count) {
		getMap().put(item, count);
		listener.forEach(BlockEntity::setChanged);
	}

	public Item item() {
		return item;
	}

	public Optional<ServerPlayer> getOwner() {
		return Proxy.getServer().map(e -> e.getPlayerList().getPlayer(id));
	}

}
