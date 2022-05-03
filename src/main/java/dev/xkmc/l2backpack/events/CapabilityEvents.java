package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.content.capability.WorldStorageCapability;
import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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

}
