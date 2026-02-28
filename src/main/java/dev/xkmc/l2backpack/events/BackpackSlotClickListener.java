package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.bag.AbstractBag;
import dev.xkmc.l2backpack.content.capability.PickupBagItem;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.remote.dimensional.DimensionalItem;
import dev.xkmc.l2backpack.content.remote.dimensional.DimensionalMenuPvd;
import dev.xkmc.l2backpack.content.remote.player.EnderBackpackItem;
import dev.xkmc.l2backpack.content.tool.IBagTool;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2backpack.init.registrate.LBTriggers;
import dev.xkmc.l2backpack.network.CreativeSetCarryToClient;
import dev.xkmc.l2menustacker.click.writable.ClickedPlayerSlotResult;
import dev.xkmc.l2menustacker.click.writable.ContainerCallback;
import dev.xkmc.l2menustacker.click.writable.WritableStackClickHandler;
import dev.xkmc.l2menustacker.init.L2MenuStacker;
import dev.xkmc.l2menustacker.screen.base.ScreenTracker;
import dev.xkmc.l2menustacker.screen.packets.CacheMouseToClient;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;

public class BackpackSlotClickListener extends WritableStackClickHandler {

	public static boolean canOpen(ItemStack stack) {
		return stack.getItem() instanceof BaseBagItem ||
				stack.getItem() instanceof EnderBackpackItem ||
				stack.getItem() instanceof DimensionalItem;
	}

	public BackpackSlotClickListener() {
		super(L2Backpack.loc("backpack"));
	}

	@Override
	public boolean isAllowed(ItemStack itemStack) {
		return canOpen(itemStack) ||
				itemStack.getItem() instanceof BaseDrawerItem ||
				itemStack.getItem() instanceof AbstractBag;
	}

	public void keyBind() {
		slotClickToServer(-1, -1, -1);
	}

	@Override
	protected ClickedPlayerSlotResult getSlot(ServerPlayer player, int index, int slot, int wid) {
		if (wid == -1) {
			if (player.containerMenu != player.inventoryMenu) {
				return null;
			}
			ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
			if (canOpen(stack)) {
				return new ClickedPlayerSlotResult(stack,
						PlayerSlot.ofInventory(36 + EquipmentSlot.CHEST.getIndex()),
						new PlayerInvCallback());
			}
			stack = player.getItemBySlot(EquipmentSlot.LEGS);
			if (canOpen(stack)) {
				return new ClickedPlayerSlotResult(stack,
						PlayerSlot.ofInventory(36 + EquipmentSlot.LEGS.getIndex()),
						new PlayerInvCallback());
			}
			if (!canOpen(stack)) {
				var pairOpt = CuriosCompat.getSlot(player, BackpackSlotClickListener::canOpen);
				if (pairOpt.isPresent()) {
					return new ClickedPlayerSlotResult(
							pairOpt.get().getFirst(),
							pairOpt.get().getSecond(),
							new PlayerInvCallback());
				}
			}
			return null;
		}
		return super.getSlot(player, index, slot, wid);
	}

	@Override
	public void handle(ServerPlayer player, int index, int slot, int wid) {
		ClickedPlayerSlotResult result = this.getSlot(player, index, slot, wid);
		if (result != null) this.handle(player, result);
		else this.handleNoMenu(player, index);
	}

	private void handleNoMenu(ServerPlayer player, int index) {
		if (index < 0) return;
		var slot = player.containerMenu.getSlot(index);
		ItemStack stack = slot.getItem();
		ItemStack carried = player.containerMenu.getCarried();
		if (carried.getItem() instanceof IBagTool tool) {
			if (stack.getItem() instanceof PickupBagItem) {
				tool.click(carried, stack);
				if (player.isCreative() && player.containerMenu.containerId == 0)
					L2Backpack.HANDLER.toClientPlayer(new CreativeSetCarryToClient(carried), player);
				return;
			}
		}
		if (!carried.isEmpty()) return;
		if (stack.getItem() instanceof BaseDrawerItem) return;
		boolean others = false;
		ScreenTracker.onServerOpen(player);
		if (stack.getItem() instanceof EnderBackpackItem) {
			player.openMenu(new SimpleMenuProvider((id, inv, pl) ->
					ChestMenu.threeRows(id, inv, pl.getEnderChestInventory()), stack.getHoverName()));
		} else if (stack.getItem() instanceof DimensionalItem chest) {
			var id = LBItems.DC_OWNER_ID.get(stack);
			others = id != null && !id.equals(player.getUUID());
			new DimensionalMenuPvd(player, stack, chest).open();
		}
		if (others) {
			LBTriggers.SHARE.get().trigger(player);
		}
	}

	@Override
	protected void handle(ServerPlayer player, ClickedPlayerSlotResult result) {
		boolean others = false;
		boolean keybind = result.container() instanceof PlayerInvCallback;
		ItemStack carried = player.containerMenu.getCarried();
		if (!keybind && carried.getItem() instanceof IBagTool tool) {
			if (result.stack().getItem() instanceof PickupBagItem) {
				tool.click(carried, result.stack());
				result.container().update();
				if (player.isCreative() && player.containerMenu.containerId == 0)
					L2Backpack.HANDLER.toClientPlayer(new CreativeSetCarryToClient(carried), player);
				return;
			}
		}
		if (!carried.isEmpty()) return;
		if (result.stack().getItem() instanceof BaseDrawerItem) return;
		if (!keybind) {
			ScreenTracker.onServerOpen(player);
		}
		switch (result.stack().getItem()) {
			case BaseBagItem bag -> {
				L2MenuStacker.PACKET_HANDLER.toClientPlayer(new CacheMouseToClient(), player);
				bag.open(player, result.slot(), result.stack());
				result.container().update();
			}
			case EnderBackpackItem ender -> {
				L2MenuStacker.PACKET_HANDLER.toClientPlayer(new CacheMouseToClient(), player);
				player.openMenu(new SimpleMenuProvider((id, inv, pl) ->
						ChestMenu.threeRows(id, inv, pl.getEnderChestInventory()),
						result.stack().getHoverName()));
			}
			case DimensionalItem chest -> {
				L2MenuStacker.PACKET_HANDLER.toClientPlayer(new CacheMouseToClient(), player);
				var id = LBItems.DC_OWNER_ID.get(result.stack());
				others = id != null && !id.equals(player.getUUID());
				new DimensionalMenuPvd(player, result.stack(), chest).open();
				result.container().update();
			}
			default -> {
			}
		}
		LBTriggers.SLOT_CLICK.get().trigger(player, result.slot().type(), keybind);
		if (others) {
			LBTriggers.SHARE.get().trigger(player);
		}
	}

	public record PlayerInvCallback() implements ContainerCallback {

		@Override
		public void update() {

		}

	}

}
