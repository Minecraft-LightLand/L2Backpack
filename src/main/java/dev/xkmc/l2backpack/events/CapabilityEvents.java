package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.registrate.LBMisc;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

@EventBusSubscriber(modid = L2Backpack.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CapabilityEvents {

	@SubscribeEvent
	public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
		if (!(event.getPlayer() instanceof ServerPlayer player)) return;
		var e = event.getItemEntity();
		var uuid = e.getTarget();
		if (e.hasPickUpDelay() || (uuid != null && !uuid.equals(player.getUUID())))
			return;
		ItemStack stack = event.getItemEntity().getItem();
		ItemStack copy = stack.copy();
		int count = stack.getCount();
		tryInsertItem(player, stack);
		if (count != stack.getCount()) {
			copy.shrink(stack.getCount());
			player.take(event.getItemEntity(), copy.getCount());
			CriteriaTriggers.INVENTORY_CHANGED.trigger(player, player.getInventory(), copy);

		}
	}

	/**
	 * if item inserted, stack is modified
	 */
	public static void tryInsertItem(ServerPlayer player, ItemStack stack) {
		ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
		var cap = chest.getCapability(LBMisc.PICKUP);
		if (cap != null) cap.doPickup(stack, new PickupTrace(false, player));
		if (stack.isEmpty()) return;
		CuriosCompat.getSlot(player, e -> {
			if (stack.isEmpty()) return false;
			var ecap = e.getCapability(LBMisc.PICKUP);
			if (ecap != null) ecap.doPickup(stack, new PickupTrace(false, player));
			return false;
		});
	}

}
