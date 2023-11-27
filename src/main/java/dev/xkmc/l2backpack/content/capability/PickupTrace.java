package dev.xkmc.l2backpack.content.capability;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Stack;

public class PickupTrace {

	private final HashSet<Integer> visited = new HashSet<>();
	private final HashSet<Integer> active = new HashSet<>();
	private final Stack<Entry> layer = new Stack<>();

	public final ServerPlayer player;

	public PickupTrace(ServerPlayer player) {
		this.player = player;
	}

	public boolean push(int sig, PickupMode mode) {
		if (visited.contains(sig)) {
			return false;
		}
		layer.push(new Entry(sig, mode));
		visited.add(sig);
		active.add(sig);
		return true;
	}

	public void pop() {
		Entry ent = layer.pop();
		active.remove(ent.sig());
	}

	public PickupMode getMode() {
		return layer.peek().mode();
	}

	private record Entry(int sig, PickupMode mode) {

	}

}
