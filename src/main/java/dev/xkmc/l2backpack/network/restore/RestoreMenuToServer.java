package dev.xkmc.l2backpack.network.restore;

import dev.xkmc.l2backpack.content.restore.ScreenTracker;
import dev.xkmc.l2backpack.content.restore.ScreenType;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class RestoreMenuToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public int wid;

	@Deprecated
	public RestoreMenuToServer() {
	}

	public RestoreMenuToServer(int wid) {
		this.wid = wid;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ServerPlayer player = context.getSender();
		if (player == null) return;
		AbstractContainerMenu menu = player.containerMenu;
		if (menu.containerId != wid || !ScreenTracker.get(player).serverRestore(player, wid)) {
			L2Backpack.HANDLER.toClientPlayer(new SetScreenToClient(ScreenType.NONE), player);
		}
	}

}
