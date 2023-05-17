package dev.xkmc.l2backpack.content.quickswap.armorswap;

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

public class ArmorBagContainer extends BaseBagContainer<ArmorBagContainer> {

	public static final SpriteManager MANAGERS = new SpriteManager(L2Backpack.MODID, "backpack_1");

	public static ArmorBagContainer fromNetwork(MenuType<ArmorBagContainer> type, int windowId, Inventory inv, FriendlyByteBuf buf) {
		PlayerSlot<?> slot = PlayerSlot.read(buf);
		UUID id = buf.readUUID();
		return new ArmorBagContainer(windowId, inv, slot, id, null);
	}

	public ArmorBagContainer(int windowId, Inventory inventory, PlayerSlot<?> hand, UUID uuid, @Nullable Component title) {
		super(BackpackMenu.MT_ARMOR.get(), windowId, inventory, MANAGERS, hand, uuid, 1, ArmorSwap::isValidItem, title);
	}

}
