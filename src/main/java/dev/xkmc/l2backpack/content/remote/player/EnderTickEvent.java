package dev.xkmc.l2backpack.content.remote.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public class EnderTickEvent extends Event {

	private final ServerPlayer player;
	private final ItemStack stack;
	private final int slot;

	public EnderTickEvent(ServerPlayer player, ItemStack stack, int slot) {
		this.player = player;
		this.stack = stack;
		this.slot = slot;
	}

	public ServerPlayer getPlayer() {
		return player;
	}

	public ItemStack getStack() {
		return stack;
	}

	public int getSlot() {
		return slot;
	}

}
