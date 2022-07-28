package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerItem;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.network.RequestTooltipUpdateEvent;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

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
		if (BaseDrawerItem.getItem(stack) == Items.AIR) return false;
		startSession(stack);
		return true;
	}

	private enum Step {
		NONE, SENT, COOLDOWN
	}

	private static final int MAX_COOLDOWN = 5;

	private static Step step = Step.NONE;
	private static UUID id = null;
	private static Item focus = null;
	private static int count = 0;
	private static int cooldown = 0;

	private static void endSession() {
		step = Step.NONE;
		focus = null;
		count = 0;
		id = null;
	}

	private static void startSession(ItemStack stack) {
		if (step == Step.NONE) {
			focus = BaseDrawerItem.getItem(stack);
			id = stack.getOrCreateTag().getUUID(EnderDrawerItem.KEY_OWNER_ID);
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

	public static void updateInfo(Item item, UUID uuid, int val) {
		if (focus != item) return;
		if (step != Step.SENT) return;
		count = val;
		id = uuid;
		step = Step.COOLDOWN;
		cooldown = MAX_COOLDOWN;
	}

	public static int getCount(UUID uuid, Item item) {
		if (id != null && focus != null && id.equals(uuid) && item == focus) {
			return count;
		}
		return -1;
	}

}
