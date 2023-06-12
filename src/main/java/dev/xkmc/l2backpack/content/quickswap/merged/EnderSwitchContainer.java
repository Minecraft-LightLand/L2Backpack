package dev.xkmc.l2backpack.content.quickswap.merged;

import dev.xkmc.l2backpack.content.common.BaseBagContainer;
import dev.xkmc.l2backpack.content.quickswap.armorswap.ArmorSwap;
import dev.xkmc.l2backpack.content.quickswap.quiver.Quiver;
import dev.xkmc.l2backpack.content.quickswap.scabbard.Scabbard;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.registrate.BackpackMenu;
import dev.xkmc.l2library.base.menu.base.SpriteManager;
import dev.xkmc.l2screentracker.screen.source.PlayerSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

public class EnderSwitchContainer extends BaseBagContainer<EnderSwitchContainer> {

	public static final SpriteManager MANAGERS = new SpriteManager(L2Backpack.MODID, "ender_switch");

	public static EnderSwitchContainer fromNetwork(MenuType<EnderSwitchContainer> type, int windowId, Inventory inv, FriendlyByteBuf buf) {
		PlayerSlot<?> slot = PlayerSlot.read(buf);
		UUID id = buf.readUUID();
		return new EnderSwitchContainer(windowId, inv, new SimpleContainer(27), slot, id, null);
	}

	public EnderSwitchContainer(int windowId, Inventory inventory, Container ender, PlayerSlot<?> hand, UUID uuid, @Nullable Component title) {
		super(BackpackMenu.MT_ES.get(), windowId, inventory, MANAGERS, hand, uuid, 3, e -> true, title);
		addEnderSlot(ender);
		addSlot("arrow", Quiver::isValidStack);
		addSlot("tool", Scabbard::isValidItem);
		addSlot("armor", ArmorSwap::isValidItem);
	}

	private int enderAdded = 0;

	protected void addEnderSlot(Container ender) {
		this.sprite.get().getSlot("ender", (x, y) -> new Slot(ender, this.enderAdded++, x, y), this::addSlot);
	}

	@Override
	public ItemStack quickMoveStack(Player pl, int id) {
		ItemStack stack = this.slots.get(id).getItem();
		if (id >= 36) {
			this.moveItemStackTo(stack, 0, 36, true);
		} else {
			if (!this.moveItemStackTo(stack, 36 + 27, 36 + 27 + 27, false))
				this.moveItemStackTo(stack, 36, 36 + 27, false);
		}
		this.container.setChanged();
		return ItemStack.EMPTY;
	}
}
