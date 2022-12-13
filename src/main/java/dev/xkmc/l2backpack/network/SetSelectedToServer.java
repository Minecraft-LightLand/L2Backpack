package dev.xkmc.l2backpack.network;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapManager;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class SetSelectedToServer extends SerialPacketBase {

	public static final int UP = -1, DOWN = -2, SWAP = -3;

	@SerialClass.SerialField
	private int slot;

	@Deprecated
	public SetSelectedToServer() {

	}

	public SetSelectedToServer(int slot) {
		this.slot = slot;
	}

	@Override
	public void handle(NetworkEvent.Context ctx) {
		Player sender = ctx.getSender();
		if (sender == null) return;
		IQuickSwapToken token = QuickSwapManager.getToken(sender);
		if (token == null) return;
		if (slot == SWAP)
			token.swap(sender);
		else
			token.setSelected(slot);
	}
}
