package dev.xkmc.l2backpack.compat;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.modulargolems.events.event.GolemEquipEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;

public class GolemCompat {

	public static void register() {
		if (ModList.get().isLoaded("modulargolems")) {
			registerImpl();
		}
	}

	private static void registerImpl() {
		MinecraftForge.EVENT_BUS.register(GolemCompat.class);
	}

	@SubscribeEvent
	public static void onEquip(GolemEquipEvent event) {
		if (event.getStack().getItem() instanceof BaseBagItem) {
			if (event.getEntity().getItemBySlot(EquipmentSlot.CHEST).isEmpty())
				event.setSlot(EquipmentSlot.CHEST, 1);
			else event.setSlot(EquipmentSlot.OFFHAND, 1);
		}
	}

}
