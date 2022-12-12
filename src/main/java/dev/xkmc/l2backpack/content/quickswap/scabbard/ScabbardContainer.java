package dev.xkmc.l2backpack.content.quickswap.scabbard;

import dev.xkmc.l2backpack.content.common.BaseBagContainer;
import dev.xkmc.l2backpack.content.common.PlayerSlot;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.registrate.BackpackMenu;
import dev.xkmc.l2library.base.menu.SpriteManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import javax.annotation.Nullable;
import java.util.UUID;

public class ScabbardContainer extends BaseBagContainer<ScabbardContainer> {

	public static final SpriteManager MANAGERS = new SpriteManager(L2Backpack.MODID, "backpack_1");

	public static ScabbardContainer fromNetwork(MenuType<ScabbardContainer> type, int windowId, Inventory inv, FriendlyByteBuf buf) {
		PlayerSlot slot = PlayerSlot.read(buf);
		UUID id = buf.readUUID();
		return new ScabbardContainer(windowId, inv, slot, id, null);
	}

	public ScabbardContainer(int windowId, Inventory inventory, PlayerSlot hand, UUID uuid, @Nullable Component title) {
		super(BackpackMenu.MT_TOOL.get(), windowId, inventory, MANAGERS, hand, uuid, 1, Scabbard::isValidItem, title);
	}

}
