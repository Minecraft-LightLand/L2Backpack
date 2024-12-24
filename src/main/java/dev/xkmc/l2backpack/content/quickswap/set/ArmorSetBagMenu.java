package dev.xkmc.l2backpack.content.quickswap.set;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2backpack.init.registrate.LBMenu;
import dev.xkmc.l2core.base.menu.base.SpriteManager;
import dev.xkmc.l2core.base.menu.data.BoolArrayDataSlot;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

public class ArmorSetBagMenu extends GenericSetSwapMenu<ArmorSetBagMenu> {

	public static final SpriteManager MANAGERS = new SpriteManager(L2Backpack.MODID, "backpack_4");

	public static ArmorSetBagMenu fromNetwork(MenuType<ArmorSetBagMenu> type, int windowId, Inventory inv, RegistryFriendlyByteBuf buf) {
		PlayerSlot<?> slot = PlayerSlot.read(buf);
		UUID id = buf.readUUID();
		return new ArmorSetBagMenu(windowId, inv, slot, id, null);
	}

	public ArmorSetBagMenu(int windowId, Inventory inventory, PlayerSlot<?> hand, UUID uuid, @Nullable Component title) {
		super(LBMenu.MT_ARMOR_SET.get(), windowId, inventory, MANAGERS, hand, uuid, 4);
	}

	@Override
	protected GenericSetBagSlot createSlot(int index, int x, int y) {
		if (!player.level().isClientSide()) {
			dataSlot.set(toggle.isLocked(index), index);
		}
		return new ArmorSetBagSlot(handler, toggle, index, x, y);
	}

}
