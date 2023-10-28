package dev.xkmc.l2backpack.content.capability;

import dev.xkmc.l2library.util.nbt.ItemCompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public interface BackpackCap {

	Capability<BackpackCap> TOKEN = CapabilityManager.get(new CapabilityToken<>() {
	});

	String KEY_ROOT = "backpack_pickup_config", KEY_MODE = "mode";

	static void register() {

	}

	static PickupMode getMode(ItemStack stack) {
		var tag = ItemCompoundTag.of(stack).getSubTag(KEY_ROOT);
		if (tag.isPresent()) {
			var ctag = tag.getOrCreate();
			if (ctag.contains(KEY_MODE)) {
				String str = ctag.getString(KEY_MODE);
				return PickupMode.valueOf(str);
			}
		}
		return PickupMode.STACKING;
	}

	PickupMode getMode();

	int doPickup(ItemStack stack, PickupTrace trace);

	int getSignature();

}
