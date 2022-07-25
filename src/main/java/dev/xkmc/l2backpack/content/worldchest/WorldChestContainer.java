package dev.xkmc.l2backpack.content.worldchest;

import dev.xkmc.l2backpack.content.backpack.BackpackContainer;
import dev.xkmc.l2backpack.content.common.BaseOpenableContainer;
import dev.xkmc.l2backpack.init.registrate.BackpackMenu;
import dev.xkmc.l2library.util.annotation.ServerOnly;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;

public class WorldChestContainer extends BaseOpenableContainer<WorldChestContainer> {

	public static WorldChestContainer fromNetwork(MenuType<WorldChestContainer> type, int windowId, Inventory inv) {
		return new WorldChestContainer(windowId, inv, new SimpleContainer(27), null, null);
	}

	protected final StorageContainer storage;
	protected final Component title;

	public WorldChestContainer(int windowId, Inventory inventory, SimpleContainer cont, @Nullable StorageContainer storage, @Nullable Component title) {
		super(BackpackMenu.MT_WORLD_CHEST.get(), windowId, inventory, BackpackContainer.MANAGERS[2], menu -> cont);
		this.addSlot("grid", stack -> true);
		this.storage = storage;
		this.title = title;
	}

	public int getColor() {
		return storage.color;
	}

	@ServerOnly
	@Override
	public boolean stillValid(Player player) {
		return storage == null || storage.isValid();
	}

	public boolean isOpenedByOwner() {
		return player.getUUID().equals(storage.id);
	}

}
