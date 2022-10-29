package dev.xkmc.l2backpack.content.restore;

import dev.xkmc.l2backpack.content.common.BaseOpenableScreen;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.network.restore.RestoreMenuToServer;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ScreenTrackerClient {

	@OnlyIn(Dist.CLIENT)
	public static boolean onClientClose(int wid) {
		ScreenTracker tracker = ScreenTracker.get(Proxy.getClientPlayer());
		if (onClientCloseImpl(tracker, wid)) {
			tracker.isWaiting = true;
			L2Backpack.HANDLER.toServer(new RestoreMenuToServer(wid));
			return true;
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	public static void onClientOpen(Screen prev, int wid, BaseOpenableScreen<?> current) {
	}

	@OnlyIn(Dist.CLIENT)
	public static void clientAddLayer(TrackedEntry entry, int toRemove, int wid) {
		ScreenTracker tracker = ScreenTracker.get(Proxy.getClientPlayer());
		for (int i = 0; i < toRemove; i++) {
			if (tracker.stack.size() > 0) {
				tracker.stack.pop();
			} else break;
		}
		tracker.wid = wid;
		tracker.stack.add(entry);
	}

	@OnlyIn(Dist.CLIENT)
	public static void clientClear(ScreenType type) {
		ScreenTracker tracker = ScreenTracker.get(Proxy.getClientPlayer());
		tracker.isWaiting = false;
		tracker.stack.clear();
		type.perform();
	}

	@OnlyIn(Dist.CLIENT)
	private static boolean onClientCloseImpl(ScreenTracker tracker, int wid) {
		if (Screen.hasShiftDown() || tracker.isWaiting) {
			// second exit: close screen
			tracker.isWaiting = false;
			return false;
		}
		if (tracker.stack.isEmpty()) return false;
		return tracker.wid == wid;
	}

	@OnlyIn(Dist.CLIENT)
	public static void clientPop(LayerPopType type, int wid) {
		ScreenTracker tracker = ScreenTracker.get(Proxy.getClientPlayer());
		tracker.isWaiting = false;
		if (type == LayerPopType.REMAIN) {
			tracker.stack.pop();
		} else {
			tracker.stack.clear();
		}
		tracker.wid = wid;
	}

}
