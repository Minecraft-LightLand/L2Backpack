package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.backpack.EnderBackpackItem;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestItem;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestMenuPvd;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.advancement.BackpackTriggers;
import dev.xkmc.l2library.init.events.click.SlotClickHandler;
import dev.xkmc.l2library.init.events.screen.base.ScreenTracker;
import dev.xkmc.l2library.init.events.screen.source.PlayerSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

public class BackpackSlotClickListener extends SlotClickHandler {

	public static boolean canOpen(ItemStack stack) {
		return stack.getItem() instanceof BaseBagItem ||
				stack.getItem() instanceof EnderBackpackItem ||
				stack.getItem() instanceof WorldChestItem;
	}

	public BackpackSlotClickListener() {
		super(new ResourceLocation(L2Backpack.MODID, "backpack"));
	}

	@Override
	public boolean isAllowed(ItemStack itemStack) {
		return canOpen(itemStack);
	}

	public void keyBind() {
		slotClickToServer(-1, -1, -1);
	}

	@Override
	public void handle(ServerPlayer player, int index, int slot, int wid) {
		ItemStack stack;
		Container container = null;
		PlayerSlot<?> playerSlot;
		AbstractContainerMenu menu = player.containerMenu;
		if (wid == -1) {
			stack = player.getItemBySlot(EquipmentSlot.CHEST);
			playerSlot = PlayerSlot.ofInventory(36 + EquipmentSlot.CHEST.getIndex());
			if (!canOpen(stack)) {
				var pairOpt = CuriosCompat.getSlot(player, BackpackSlotClickListener::canOpen);
				if (pairOpt.isPresent()) {
					stack = pairOpt.get().getFirst();
					playerSlot = pairOpt.get().getSecond();
				}
			}
		} else if (slot >= 0) {
			stack = player.getInventory().getItem(slot);
			playerSlot = PlayerSlot.ofInventory(slot);
		} else {
			if (wid == 0 || menu.containerId == 0 || wid != menu.containerId) return;
			playerSlot = PlayerSlot.ofOtherInventory(slot, index, wid, menu);
			stack = menu.getSlot(index).getItem();
			container = menu.getSlot(index).container;
		}
		boolean others = false;
		if (playerSlot != null) {
			if (wid != -1 || slot != -1 || index != -1) {
				ScreenTracker.onServerOpen(player);
			}
		}
		if (playerSlot != null && stack.getItem() instanceof BaseBagItem bag) {
			bag.open(player, playerSlot, stack);
		} else if (stack.getItem() instanceof EnderBackpackItem) {
			NetworkHooks.openScreen(player, new SimpleMenuProvider((id, inv, pl) ->
					ChestMenu.threeRows(id, inv, pl.getEnderChestInventory()), stack.getHoverName()));
		} else if (stack.getItem() instanceof WorldChestItem chest) {
			others = WorldChestItem.getOwner(stack).map(e -> !e.equals(player.getUUID())).orElse(false);
			new WorldChestMenuPvd(player, stack, chest).open();
			if (container != null) {
				container.setChanged();
			}
		}
		if (playerSlot != null) {
			BackpackTriggers.SLOT_CLICK.trigger(player, playerSlot.type(), wid == -1 && slot == -1 && index == -1);
		}
		if (others) {
			BackpackTriggers.SHARE.trigger(player);
		}
	}

}
