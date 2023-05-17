package dev.xkmc.l2backpack.content.backpack;

import dev.xkmc.l2backpack.content.common.BaseBagContainer;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.registrate.BackpackMenu;
import dev.xkmc.l2library.base.menu.SpriteManager;
import dev.xkmc.l2library.init.events.screen.source.PlayerSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;
import java.util.UUID;

public class BackpackContainer extends BaseBagContainer<BackpackContainer> {

	public static final SpriteManager[] MANAGERS = new SpriteManager[6];

	static {
		for (int i = 0; i < 6; i++) {
			MANAGERS[i] = new SpriteManager(L2Backpack.MODID, "backpack_" + (i + 1));
		}
	}

	public static BackpackContainer fromNetwork(MenuType<BackpackContainer> type, int windowId, Inventory inv, FriendlyByteBuf buf) {
		PlayerSlot<?> slot = PlayerSlot.read(buf);
		UUID id = buf.readUUID();
		int row = buf.readInt();
		return new BackpackContainer(windowId, inv, slot, id, row, null);
	}

	public BackpackContainer(int windowId, Inventory inventory, PlayerSlot<?> hand, UUID uuid, int row, @Nullable Component title) {
		super(BackpackMenu.MT_BACKPACK.get(), windowId, inventory, MANAGERS[row - 1], hand, uuid, row,
				stack -> stack.getItem().canFitInsideContainerItems(), title);
	}

}
