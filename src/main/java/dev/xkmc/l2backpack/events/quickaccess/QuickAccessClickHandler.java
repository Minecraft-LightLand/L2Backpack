package dev.xkmc.l2backpack.events.quickaccess;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class QuickAccessClickHandler {

	private static final Map<Item, QuickAccessAction> MAP = new HashMap<>();

	public static synchronized void register(Item item, QuickAccessAction action) {
		MAP.put(item, action);
	}

	public static boolean isAllowed(ItemStack stack) {
		return MAP.get(stack.getItem()) != null;
	}

	public static void handle(ServerPlayer player, ItemStack stack) {
		QuickAccessAction accessAction = MAP.get(stack.getItem());
		if (accessAction != null) {
			accessAction.perform(player, stack);
		}
	}
}
