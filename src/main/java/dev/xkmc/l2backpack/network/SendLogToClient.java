package dev.xkmc.l2backpack.network;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class SendLogToClient extends SerialPacketBase {

	@SerialClass.SerialField
	public String str;

	@Deprecated
	public SendLogToClient() {

	}

	public SendLogToClient(String str) {
		this.str = str;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		L2Backpack.LOGGER.warn(str);
	}
}
