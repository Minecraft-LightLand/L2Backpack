package dev.xkmc.l2backpack.network.restore;

import dev.xkmc.l2backpack.content.restore.ScreenTrackerClient;
import dev.xkmc.l2backpack.content.restore.TrackedEntry;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class AddTrackedToClient extends SerialPacketBase {

	@SerialClass.SerialField
	public TrackedEntry entry;

	@SerialClass.SerialField
	public int toRemove, wid;

	@Deprecated
	public AddTrackedToClient() {

	}

	public AddTrackedToClient(TrackedEntry entry, int toRemove, int wid) {
		this.entry = entry;
		this.toRemove = toRemove;
		this.wid = wid;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ScreenTrackerClient.clientAddLayer(entry, toRemove, wid);
	}
}
