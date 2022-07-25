package dev.xkmc.l2backpack.content.backpack;

import dev.xkmc.l2backpack.content.common.BaseBagContainer;
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
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class BackpackContainer extends BaseBagContainer<BackpackContainer> {

	public static final SpriteManager[] MANAGERS = new SpriteManager[6];

	static {
		for (int i = 0; i < 6; i++) {
			MANAGERS[i] = new SpriteManager(L2Backpack.MODID, "backpack_" + (i + 1));
		}
	}

	public static BackpackContainer fromNetwork(MenuType<BackpackContainer> type, int windowId, Inventory inv, FriendlyByteBuf buf) {
		int slot = buf.readInt();
		UUID id = buf.readUUID();
		int row = buf.readInt();
		return new BackpackContainer(windowId, inv, slot, id, row);
	}

	public BackpackContainer(int windowId, Inventory inventory, int hand, UUID uuid, int row) {
		super(BackpackMenu.MT_BACKPACK.get(), windowId, inventory, MANAGERS[row - 1], hand, uuid, row, stack -> stack.getItem().canFitInsideContainerItems());
	}

}
