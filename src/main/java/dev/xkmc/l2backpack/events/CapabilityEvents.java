package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.capability.BackpackCap;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import dev.xkmc.l2backpack.content.remote.common.WorldStorageCapability;
import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = L2Backpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityEvents {

	@SubscribeEvent
	public static void onAttachLevelCapabilities(AttachCapabilitiesEvent<Level> event) {
		if (event.getObject() instanceof ServerLevel level) {
			if (level.dimension() == Level.OVERWORLD) {
				event.addCapability(new ResourceLocation(L2Backpack.MODID, "world_storage"),
						new WorldStorageCapability(level));
			}
		}
	}

	@SubscribeEvent
	public static void onItemPickup(EntityItemPickupEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) return;
		ItemStack stack = event.getItem().getItem();
		tryInsertItem(player, stack);
	}

	/**
	 * if item inserted, stack is modified
	 */
	public static void tryInsertItem(ServerPlayer player, ItemStack stack) {
		ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
		chest.getCapability(BackpackCap.TOKEN).resolve().ifPresent(
				cap -> cap.doPickup(stack, new PickupTrace(player)));
		if (stack.isEmpty()) return;
		CuriosCompat.getSlot(player, e -> {
			if (stack.isEmpty()) return false;
			e.getCapability(BackpackCap.TOKEN).resolve().ifPresent(
					cap -> cap.doPickup(stack, new PickupTrace(player)));
			return false;
		});
	}

}
