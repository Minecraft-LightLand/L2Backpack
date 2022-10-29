package dev.xkmc.l2backpack.network.restore;

import dev.xkmc.l2backpack.content.restore.LayerPopType;
import dev.xkmc.l2backpack.content.restore.ScreenTrackerClient;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class PopLayerToClient extends SerialPacketBase {

	@SerialClass.SerialField
	public LayerPopType type;

	@SerialClass.SerialField
	public int wid;

	@Deprecated
	public PopLayerToClient() {

	}

	public PopLayerToClient(LayerPopType type, int wid) {
		this.type = type;
		this.wid = wid;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ScreenTrackerClient.clientPop(type, wid);
	}

}
