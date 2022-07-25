package dev.xkmc.l2backpack.content.arrowbag;

import dev.xkmc.l2backpack.content.common.BaseBagContainer;
import dev.xkmc.l2backpack.content.common.PlayerSlot;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.registrate.BackpackMenu;
import dev.xkmc.l2library.base.menu.SpriteManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArrowItem;

import java.util.UUID;

public class ArrowBagContainer extends BaseBagContainer<ArrowBagContainer> {

	public static final SpriteManager MANAGERS = new SpriteManager(L2Backpack.MODID, "backpack_1");

	public static ArrowBagContainer fromNetwork(MenuType<ArrowBagContainer> type, int windowId, Inventory inv, FriendlyByteBuf buf) {
		PlayerSlot slot = PlayerSlot.read(buf);
		UUID id = buf.readUUID();
		return new ArrowBagContainer(windowId, inv, slot, id);
	}

	public ArrowBagContainer(int windowId, Inventory inventory, PlayerSlot hand, UUID uuid) {
		super(BackpackMenu.MT_ARROW.get(), windowId, inventory, MANAGERS, hand, uuid, 1, stack -> stack.getItem() instanceof ArrowItem);
	}

}
