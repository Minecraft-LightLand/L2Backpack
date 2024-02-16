package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.capability.PickupBagItem;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.remote.player.EnderBackpackItem;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestItem;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestMenuPvd;
import dev.xkmc.l2backpack.content.tool.IBagTool;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.advancement.BackpackTriggers;
import dev.xkmc.l2screentracker.click.writable.ClickedPlayerSlotResult;
import dev.xkmc.l2screentracker.click.writable.ContainerCallback;
import dev.xkmc.l2screentracker.click.writable.WritableStackClickHandler;
import dev.xkmc.l2screentracker.screen.base.ScreenTracker;
import dev.xkmc.l2screentracker.screen.source.PlayerSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

public class BackpackSlotClickListener extends WritableStackClickHandler {

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
		return canOpen(itemStack) || itemStack.getItem() instanceof BaseDrawerItem;
	}

	public void keyBind() {
		slotClickToServer(-1, -1, -1);
	}

	@Override
	protected ClickedPlayerSlotResult getSlot(ServerPlayer player, int index, int slot, int wid) {
		if (wid == -1) {
			ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
			if (canOpen(stack)) {
				return new ClickedPlayerSlotResult(stack,
						PlayerSlot.ofInventory(36 + EquipmentSlot.CHEST.getIndex()),
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
		ItemStack stack = player.containerMenu.getSlot(index).getItem();
		ItemStack carried = player.containerMenu.getCarried();
		if (carried.getItem() instanceof IBagTool tool) {
			if (stack.getItem() instanceof PickupBagItem) {
				tool.click(stack);
				return;
			}
		}
		if (!carried.isEmpty()) return;
		if (stack.getItem() instanceof BaseDrawerItem) return;
		boolean others = false;
		ScreenTracker.onServerOpen(player);
		if (stack.getItem() instanceof EnderBackpackItem) {
			NetworkHooks.openScreen(player, new SimpleMenuProvider((id, inv, pl) ->
					ChestMenu.threeRows(id, inv, pl.getEnderChestInventory()), stack.getHoverName()));
		} else if (stack.getItem() instanceof WorldChestItem chest) {
			others = WorldChestItem.getOwner(stack).map(e -> !e.equals(player.getUUID())).orElse(false);
			new WorldChestMenuPvd(player, stack, chest).open();
		}
		if (others) {
			BackpackTriggers.SHARE.trigger(player);
		}
	}

	@Override
	protected void handle(ServerPlayer player, ClickedPlayerSlotResult result) {
		boolean others = false;
		boolean keybind = result.container() instanceof PlayerInvCallback;
		ItemStack carried = player.containerMenu.getCarried();
		if (!keybind && carried.getItem() instanceof IBagTool tool) {
			if (result.stack().getItem() instanceof PickupBagItem) {
				tool.click(result.stack());
				result.container().update();
				return;
			}
		}
		if (!carried.isEmpty()) return;
		if (result.stack().getItem() instanceof BaseDrawerItem) return;
		if (!keybind) {
			ScreenTracker.onServerOpen(player);
		}
		if (result.stack().getItem() instanceof BaseBagItem bag) {
			bag.open(player, result.slot(), result.stack());
			result.container().update();
		} else if (result.stack().getItem() instanceof EnderBackpackItem) {
			NetworkHooks.openScreen(player, new SimpleMenuProvider((id, inv, pl) ->
					ChestMenu.threeRows(id, inv, pl.getEnderChestInventory()), result.stack().getHoverName()));
		} else if (result.stack().getItem() instanceof WorldChestItem chest) {
			others = WorldChestItem.getOwner(result.stack()).map(e -> !e.equals(player.getUUID())).orElse(false);
			new WorldChestMenuPvd(player, result.stack(), chest).open();
			result.container().update();
		}
		BackpackTriggers.SLOT_CLICK.trigger(player, result.slot().type(), keybind);
		if (others) {
			BackpackTriggers.SHARE.trigger(player);
		}
	}

	public record PlayerInvCallback() implements ContainerCallback {

		@Override
		public void update() {

		}

	}

}
