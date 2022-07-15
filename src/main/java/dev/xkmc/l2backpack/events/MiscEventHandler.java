package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2backpack.content.backpack.BackpackItem;
import dev.xkmc.l2backpack.content.backpack.EnderBackpackItem;
import dev.xkmc.l2backpack.content.worldchest.WorldChestItem;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MiscEventHandler {

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onScreenClick(ScreenEvent.MouseButtonPressed.Pre event) {
		Screen screen = event.getScreen();
		if (event.getButton() == 1 &&
				screen instanceof AbstractContainerScreen cont) {
			Slot slot = cont.findSlot(event.getMouseX(), event.getMouseY());
			boolean b0 = slot != null;
			boolean b1 = b0 && slot.container == Proxy.getClientPlayer().getInventory();
			boolean b2 = b0 && cont.getMenu().containerId > 0;
			if (b1 || b2) {
				int inv = b1 ? slot.getSlotIndex() : -1;
				int ind = inv == -1 ? slot.index : -1;
				int wid = cont.getMenu().containerId;
				if ((inv >= 0 || ind >= 0) && (slot.getItem().getItem() instanceof EnderBackpackItem ||
						slot.getItem().getItem() instanceof WorldChestItem ||
						inv >= 0 && slot.getItem().getItem() instanceof BackpackItem)) {
					L2Backpack.HANDLER.toServer(new SlotClickToServer(ind, inv, wid));
					event.setCanceled(true);
				}
			}
		}

	}

}
