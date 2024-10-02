package dev.xkmc.l2backpack.content.quickswap.set;

import dev.xkmc.l2backpack.content.common.BaseBagMenu;
import dev.xkmc.l2backpack.content.quickswap.common.ISetSwapItem;
import dev.xkmc.l2core.base.menu.base.SpriteManager;
import dev.xkmc.l2core.base.menu.data.BoolArrayDataSlot;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class GenericSetSwapMenu<T extends GenericSetSwapMenu<T>> extends BaseBagMenu<T> {

	protected ISetToggle toggle;
	protected BoolArrayDataSlot dataSlot;

	public GenericSetSwapMenu(MenuType<T> type, int windowId, Inventory inventory, SpriteManager manager, PlayerSlot<?> hand, UUID uuid, int row) {
		super(type, windowId, inventory, manager, hand, uuid, row);
	}

	@Override
	protected void addSlots() {
		dataSlot = new BoolArrayDataSlot(this, row * 9);
		toggle = createToggle(getStack(), dataSlot);
		super.addSlots();
	}

	protected ISetToggle createToggle(ItemStack stack, BoolArrayDataSlot dataSlot) {
		if (player.level().isClientSide())
			return new ClientSetToggle(dataSlot);
		return ((ISetSwapItem) stack.getItem()).getToggle(stack, dataSlot);
	}

	@Override
	protected GenericSetBagSlot createSlot(int index, int x, int y) {
		if (!player.level().isClientSide()) {
			dataSlot.set(toggle.isLocked(index), index);
		}
		return new GenericSetBagSlot(handler, toggle, index, x, y);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (id >= 0 && id < row * 9) {
			if (!player.level().isClientSide()) {
				if (getSlot(36 + id) instanceof GenericSetBagSlot slot) {
					if (slot.getItem().isEmpty()) {
						slot.toggle();
					}
				}
			}
			return true;
		}
		return false;
	}

}
