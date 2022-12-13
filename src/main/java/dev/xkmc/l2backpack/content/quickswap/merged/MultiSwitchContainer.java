package dev.xkmc.l2backpack.content.quickswap.merged;

import dev.xkmc.l2backpack.content.common.BaseBagContainer;
import dev.xkmc.l2backpack.content.common.PlayerSlot;
import dev.xkmc.l2backpack.content.quickswap.armorswap.ArmorSwap;
import dev.xkmc.l2backpack.content.quickswap.quiver.Quiver;
import dev.xkmc.l2backpack.content.quickswap.scabbard.Scabbard;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.registrate.BackpackMenu;
import dev.xkmc.l2library.base.menu.SpriteManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;
import java.util.UUID;

public class MultiSwitchContainer extends BaseBagContainer<MultiSwitchContainer> {

	public static final SpriteManager MANAGERS = new SpriteManager(L2Backpack.MODID, "multi_switch");

	public static MultiSwitchContainer fromNetwork(MenuType<MultiSwitchContainer> type, int windowId, Inventory inv, FriendlyByteBuf buf) {
		PlayerSlot slot = PlayerSlot.read(buf);
		UUID id = buf.readUUID();
		return new MultiSwitchContainer(windowId, inv, slot, id, null);
	}

	public MultiSwitchContainer(int windowId, Inventory inventory, PlayerSlot hand, UUID uuid, @Nullable Component title) {
		super(BackpackMenu.MT_MULTI.get(), windowId, inventory, MANAGERS, hand, uuid, 3, e -> true, title);
		addSlot("arrow", Quiver::isValidStack);
		addSlot("tool", Scabbard::isValidItem);
		addSlot("arrow", ArmorSwap::isValidItem);
	}

}
