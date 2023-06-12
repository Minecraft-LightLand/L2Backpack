package dev.xkmc.l2backpack.compat;

import net.minecraftforge.common.MinecraftForge;

public class GolemCompat {

	public static void register() {
		MinecraftForge.EVENT_BUS.register(GolemCompat.class);
	}

	/*
	@SubscribeEvent
	public static void onEquip(GolemEquipEvent event) {
		if (event.getStack().getItem() instanceof BaseBagItem) {
			ItemStack back = event.getEntity().getItemBySlot(EquipmentSlot.CHEST);
			if (back.isEmpty() || back.getItem() instanceof BaseBagItem)
				event.setSlot(EquipmentSlot.CHEST, 1);
			else event.setSlot(EquipmentSlot.OFFHAND, 1);
		}
	}

	 */

}
