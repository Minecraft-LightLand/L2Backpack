package dev.xkmc.l2backpack.network.restore;

import dev.xkmc.l2backpack.content.restore.ScreenTrackerClient;
import dev.xkmc.l2backpack.content.restore.ScreenType;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class SetScreenToClient extends SerialPacketBase {

	@SerialClass.SerialField
	public ScreenType type;

	@Deprecated
	public SetScreenToClient() {
	}

	public SetScreenToClient(ScreenType type) {
		this.type = type;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ScreenTrackerClient.clientClear(type);
	}

}
