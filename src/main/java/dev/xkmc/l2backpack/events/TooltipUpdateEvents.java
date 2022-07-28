package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerItem;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import dev.xkmc.l2backpack.network.RequestTooltipUpdateEvent;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TooltipUpdateEvents {

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		if (!continueSession()) {
			endSession();
		}
	}

	private static boolean continueSession() {
		Screen screen = Minecraft.getInstance().screen;
		if (!(screen instanceof AbstractContainerScreen<?> cont)) return false;
		Slot slot = cont.getSlotUnderMouse();
		if (slot == null) return false;
		ItemStack stack = slot.getItem();
		if (!(stack.getItem() instanceof EnderDrawerItem)) return false;
		startSession(stack);
		return true;
	}

	private enum Step {
		NONE, SENT, COOLDOWN
	}

	private static final int MAX_COOLDOWN = 5;

	private static Step step = Step.NONE;
	private static Item focus = null;
	private static int count = 0;
	private static int cooldown = 0;

	private static void endSession() {
		step = Step.NONE;
		focus = null;
		count = 0;
	}

	private static void startSession(ItemStack stack) {
		if (step == Step.NONE) {
			focus = BaseDrawerItem.getItem(stack);
			step = Step.SENT;
			L2Backpack.HANDLER.toServer(new RequestTooltipUpdateEvent(focus, Proxy.getClientPlayer().getUUID()));
		} else if (step == Step.COOLDOWN) {
			if (cooldown > 0) {
				cooldown--;
			}
			if (cooldown <= 0) {
				cooldown = 0;
				step = Step.NONE;
			}
		}
	}

	public static void updateInfo(Item item, int val) {
		if (focus != item) return;
		if (step != Step.SENT) return;
		count = val;
		step = Step.COOLDOWN;
		cooldown = MAX_COOLDOWN;
	}

	public static int getCount(Item item){
		if (item == focus){
			return count;
		}
		return -1;
	}

}
