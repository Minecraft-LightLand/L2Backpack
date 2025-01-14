package dev.xkmc.l2backpack.content.remote.common;

import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerBlockEntity;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.l2core.util.ServerProxy;
import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;

public final class EnderDrawerAccess {

	public static EnderDrawerAccess of(Level level, ItemStack drawer) {
		UUID id = LBItems.DC_OWNER_ID.getOrDefault(drawer, Util.NIL_UUID);
		Item item = LBItems.DC_ENDER_DRAWER_ITEM.getOrDefault(drawer, Items.AIR);
		return of(level, id, item);
	}


	public static EnderDrawerAccess of(Level level, UUID id, Item item) {
		return LBSavedData.get((ServerLevel) level).getOrCreateDrawer(id, item);
	}

	private final LBSavedData storage;
	private final UUID id;
	private final Item item;

	public final List<EnderDrawerBlockEntity> listener = new ArrayList<>();

	EnderDrawerAccess(LBSavedData storage, UUID id, Item item) {
		this.storage = storage;
		this.id = id;
		this.item = item;
	}

	private HashMap<Item, Integer> getMap() {
		return storage.get(id).drawer;
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
		return ServerProxy.getServer().map(e -> e.getPlayerList().getPlayer(id));
	}

}
