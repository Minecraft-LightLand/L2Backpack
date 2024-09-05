package dev.xkmc.l2backpack.content.remote.dimensional;

import dev.xkmc.l2backpack.content.backpack.BackpackMenu;
import dev.xkmc.l2backpack.content.click.DrawerQuickInsert;
import dev.xkmc.l2backpack.content.remote.common.StorageContainer;
import dev.xkmc.l2backpack.init.registrate.LBMenu;
import dev.xkmc.l2core.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2core.util.ServerOnly;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

public class DimensionalContainer extends BaseContainerMenu<DimensionalContainer> implements DrawerQuickInsert {

	public static DimensionalContainer fromNetwork(MenuType<DimensionalContainer> type, int windowId, Inventory inv) {
		return new DimensionalContainer(windowId, inv, new SimpleContainer(54), null, null);
	}

	protected final Player player;

	@Nullable
	protected final StorageContainer storage;

	@Nullable
	private final DimensionalBlockEntity activeChest;

	public DimensionalContainer(int windowId, Inventory inventory, SimpleContainer cont,
								@Nullable StorageContainer storage,
								@Nullable DimensionalBlockEntity entity) {
		super(LBMenu.MT_WORLD_CHEST.get(), windowId, inventory, BackpackMenu.MANAGERS[5], menu -> cont, false);
		this.player = inventory.player;
		this.addSlot("grid", stack -> true);
		this.storage = storage;
		this.activeChest = entity;
	}

	@ServerOnly
	public int getColor() {
		assert storage != null;
		return storage.color;
	}

	@ServerOnly
	public UUID getOwner() {
		assert storage != null;
		return storage.id;
	}

	@ServerOnly
	@Override
	public boolean stillValid(Player player) {
		if (activeChest != null) {
			if (!this.activeChest.stillValid(player)) {
				return false;
			}
		}
		return storage != null;
	}

	public ItemStack quickMoveStack(Player pl, int id) {
		ItemStack stack = this.slots.get(id).getItem();
		if (quickMove(pl, this, stack, id)) {
			this.slots.get(id).setChanged();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean quickMove(Player pl, AbstractContainerMenu menu, ItemStack stack, int id) {
		int n = this.container.getContainerSize();
		boolean moved;
		if (id >= 36) {
			moved = DrawerQuickInsert.moveItemStackTo(pl, this, stack, 0, 36, true);
		} else {
			moved = DrawerQuickInsert.moveItemStackTo(pl, this, stack, 36, 36 + n, false);
		}
		return moved;
	}

}
