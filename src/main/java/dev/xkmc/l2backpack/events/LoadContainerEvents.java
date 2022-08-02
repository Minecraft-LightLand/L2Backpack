package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.content.common.ContentTransfer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class LoadContainerEvents {


	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void playerLeftClick(@NotNull PlayerInteractEvent.LeftClickBlock event) {
		if (event.getEntity().isShiftKeyDown() && event.getItemStack().getItem() instanceof ContentTransfer.Quad load) {
			ContentTransfer.leftClick(load, event.getLevel(), event.getPos(), event.getItemStack(), event.getEntity());
			event.setCanceled(true);
		}
	}

}
