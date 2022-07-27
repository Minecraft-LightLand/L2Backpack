package dev.xkmc.l2backpack.network;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class DrawerInteractToServer extends SerialPacketBase {

	public enum Type {
		INSERT, TAKE
	}

	@Override
	public void handle(NetworkEvent.Context context) {

	}

}
