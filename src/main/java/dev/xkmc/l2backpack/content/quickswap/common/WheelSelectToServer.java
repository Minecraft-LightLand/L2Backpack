package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapManager;
import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;

public record WheelSelectToServer(
		int slot, int index
) implements SerialPacketBase<WheelSelectToServer> {

	public static WheelSelectToServer of(int slot, int index) {
		return new WheelSelectToServer(slot, index);
	}

	public void handle(Player player) {
		var list = QuickSwapManager.getTokens(player, null, false);
		if (list.size() <= index) return;
		var token = list.get(index);
		if (!token.type().supportWheel()) return;
		token.setSelected(slot);
		token.swap(player);
	}
}
