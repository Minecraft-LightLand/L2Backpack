package dev.xkmc.l2backpack.content.quickswap.handswap;

import dev.xkmc.l2backpack.content.common.BaseBagMenu;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.registrate.BackpackMenus;
import dev.xkmc.l2library.base.menu.base.SpriteManager;
import dev.xkmc.l2screentracker.screen.source.PlayerSlot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.UUID;

public class HandswapMenu extends BaseBagMenu<HandswapMenu> {

	public static final SpriteManager MANAGERS = new SpriteManager(L2Backpack.MODID, "handswap");

	public static HandswapMenu fromNetwork(MenuType<HandswapMenu> type, int windowId, Inventory inv, FriendlyByteBuf buf) {
		PlayerSlot<?> slot = PlayerSlot.read(buf);
		UUID id = buf.readUUID();
		return new HandswapMenu(windowId, inv, slot, id, null);
	}

	protected int[] vals = new int[9];

	public HandswapMenu(int windowId, Inventory inventory, PlayerSlot<?> hand, UUID uuid, @Nullable Component title) {
		super(BackpackMenus.MT_HAND.get(), windowId, inventory, MANAGERS, hand, uuid, 1);
		var stack = getStack();
		var list = HandswapItem.getMatcher(stack);
		for (int i = 0; i < 9; i++) {
			vals[i] = BuiltInRegistries.ITEM.getId(list.get(i));
			addDataSlot(DataSlot.shared(vals, i));
		}
	}

	@Override
	public boolean clickMenuButton(Player player, int i) {
		if (i < 0 || i >= 9) return false;
		if (player.level().isClientSide())
			return true;
		var stack = getStack();
		var list = HandswapItem.getMatcher(stack);
		list.set(i, Items.AIR);
		vals[i] = 0;
		HandswapItem.setMatcher(stack, list);

		return true;
	}

	public Slot getSlot(String name, int i, int j) {
		return super.getSlot(name, i, j);
	}

	public Item getMatcher(int i) {
		return BuiltInRegistries.ITEM.byId(vals[i]);
	}

}
