package dev.xkmc.l2backpack.content.item;

import dev.xkmc.l2backpack.content.capability.StorageContainer;
import dev.xkmc.l2backpack.init.registrate.LightlandMenu;
import dev.xkmc.l2library.menu.BaseContainerMenu;
import dev.xkmc.l2library.util.ServerOnly;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;

public class WorldChestContainer extends BaseContainerMenu<WorldChestContainer> {

	public static WorldChestContainer fromNetwork(MenuType<WorldChestContainer> type, int windowId, Inventory inv) {
		return new WorldChestContainer(windowId, inv, new SimpleContainer(27), null);
	}

	protected final Player player;
	protected final StorageContainer storage;

	public WorldChestContainer(int windowId, Inventory inventory, SimpleContainer cont, @Nullable StorageContainer storage) {
		super(LightlandMenu.MT_WORLD_CHEST.get(), windowId, inventory, BackpackContainer.MANAGERS[2], menu -> cont, false);
		this.player = inventory.player;
		this.addSlot("grid", stack -> true);
		this.storage = storage;
	}

	@ServerOnly
	@Override
	public boolean stillValid(Player player) {
		return storage == null || storage.isValid();
	}

}
