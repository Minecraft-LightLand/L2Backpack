package dev.xkmc.l2backpack.compat;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.modulargolems.events.event.GolemEquipEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GolemCompat {

	public static void register() {
		MinecraftForge.EVENT_BUS.register(GolemCompat.class);
	}

	@SubscribeEvent
	public static void onEquip(GolemEquipEvent event) {
		if (event.getStack().getItem() instanceof BaseBagItem) {
			ItemStack back = event.getEntity().getItemBySlot(EquipmentSlot.CHEST);
			if (back.isEmpty() || back.getItem() instanceof BaseBagItem)
				event.setSlot(EquipmentSlot.CHEST, 1);
			else event.setSlot(EquipmentSlot.OFFHAND, 1);
		}
	}

}
