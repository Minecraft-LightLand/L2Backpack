package dev.xkmc.l2backpack.network;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapManager;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class SetArrowToServer extends SerialPacketBase {

	@SerialClass.SerialField
	private int slot;

	@Deprecated
	public SetArrowToServer() {

	}

	public SetArrowToServer(int slot) {
		this.slot = slot;
	}

	@Override
	public void handle(NetworkEvent.Context ctx) {
		Player sender = ctx.getSender();
		if (sender == null) return;
		IQuickSwapToken token = QuickSwapManager.getToken(sender);
		if (token == null) return;
		token.setSelected(slot);
	}
}
