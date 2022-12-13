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
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

import javax.annotation.Nullable;
import java.util.UUID;

public class EnderSwitchContainer extends BaseBagContainer<EnderSwitchContainer> {

	public static final SpriteManager MANAGERS = new SpriteManager(L2Backpack.MODID, "ender_switch");

	public static EnderSwitchContainer fromNetwork(MenuType<EnderSwitchContainer> type, int windowId, Inventory inv, FriendlyByteBuf buf) {
		PlayerSlot slot = PlayerSlot.read(buf);
		UUID id = buf.readUUID();
		return new EnderSwitchContainer(windowId, inv, new SimpleContainer(27), slot, id, null);
	}

	public EnderSwitchContainer(int windowId, Inventory inventory, Container ender, PlayerSlot hand, UUID uuid, @Nullable Component title) {
		super(BackpackMenu.MT_ES.get(), windowId, inventory, MANAGERS, hand, uuid, 3, e -> true, title);
		addEnderSlot(ender);
		addSlot("arrow", Quiver::isValidStack);
		addSlot("tool", Scabbard::isValidItem);
		addSlot("armor", ArmorSwap::isValidItem);
	}

	private int enderAdded = 0;

	protected void addEnderSlot(Container ender) {
		this.sprite.getSlot("ender", (x, y) -> new Slot(ender, this.enderAdded++, x, y), this::addSlot);
	}

}
