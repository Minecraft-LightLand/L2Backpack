package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapManager;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class WheelSelectToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public int slot, index;
	@SerialClass.SerialField
	public boolean shift;

	public WheelSelectToServer() {
		super();
	}

	public WheelSelectToServer(int slot, int index, boolean shift) {
		this.slot = slot;
		this.index = index;
		this.shift = shift;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var player = context.getSender();
		if (player == null) return;
		var list = QuickSwapManager.getWheelTokens(player, shift);
		if (list.size() <= index) return;
		var token = list.get(index);
		if (!token.type().supportWheel()) return;
		token.setSelected(slot);
		token.swap(player);
	}

}
