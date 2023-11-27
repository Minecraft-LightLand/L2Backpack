package dev.xkmc.l2backpack.content.capability;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public interface PickupModeCap {

	Capability<PickupModeCap> TOKEN = CapabilityManager.get(new CapabilityToken<>() {
	});

	static void register() {
	}

	PickupConfig getPickupMode();

	int doPickup(ItemStack stack, PickupTrace trace);

	int getSignature();

}
