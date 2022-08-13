package dev.xkmc.l2backpack.content.restore;

import dev.xkmc.l2backpack.content.common.BaseOpenableContainer;
import dev.xkmc.l2backpack.content.common.BaseOpenableScreen;
import dev.xkmc.l2backpack.content.common.ContainerType;
import dev.xkmc.l2backpack.content.common.PlayerSlot;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestContainer;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.network.restore.AddTrackedToClient;
import dev.xkmc.l2backpack.network.restore.PopLayerToClient;
import dev.xkmc.l2backpack.network.restore.RestoreMenuToServer;
import dev.xkmc.l2library.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2library.capability.player.PlayerCapabilityNetworkHandler;
import dev.xkmc.l2library.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import javax.annotation.Nullable;
import java.util.Stack;

/**
 * 1. Server open BaseOpenableContainer: add previous and current menu to stack, tell client
 * 2. Client open BaseOpenableContainer: record title of previous and current title to stack
 */
@SerialClass
@SuppressWarnings("unused")
public class ScreenTracker extends PlayerCapabilityTemplate<ScreenTracker> {

	public static final Capability<ScreenTracker> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});

	public static final PlayerCapabilityHolder<ScreenTracker> HOLDER = new PlayerCapabilityHolder<>(
			new ResourceLocation(L2Backpack.MODID, "screen_tracker"), CAPABILITY,
			ScreenTracker.class, ScreenTracker::new, PlayerCapabilityNetworkHandler::new);

	public static void register() {
	}

	public static ScreenTracker get(Player player) {
		return HOLDER.get(player);
	}

	public static boolean onClientClose(int wid) {
		ScreenTracker tracker = get(Proxy.getClientPlayer());
		if (tracker.onClientCloseImpl(wid)) {
			tracker.isWaiting = true;
			L2Backpack.HANDLER.toServer(new RestoreMenuToServer(wid));
			return true;
		}
		return false;
	}

	public static void onClientOpen(Screen prev, int wid, BaseOpenableScreen<?> current) {
	}

	public static void onServerOpen(ServerPlayer player, AbstractContainerMenu prev, PlayerSlot slot) {
		if (!(player.containerMenu instanceof BaseOpenableContainer<?> cont)) return;
		Component comp = prev instanceof BaseOpenableContainer<?> prevOpen ? prevOpen.title : null;
		get(player).serverOpen(player, formPlayerSlot(slot, comp), cont);
	}

	public static void onServerClose(Player player, int wid) {

	}

	private static TrackedEntry formPlayerSlot(PlayerSlot slot, @Nullable Component title) {
		int color = slot.type() == ContainerType.DIMENSION ? slot.slot() / 27 : 0;
		return TrackedEntry.of(slot.type(), slot.uuid(), color, title);
	}

	// non-static

	private final Stack<TrackedEntry> stack = new Stack<>();

	private int wid;

	// --- server only values

	private BaseOpenableContainer<?> current;

	private void serverOpen(ServerPlayer player, TrackedEntry prev, BaseOpenableContainer<?> cont) {
		int toRemove = 0;
		if (!stack.isEmpty()) {
			if (!prev.match(current)) {
				toRemove = stack.size();
				stack.clear();
			} else {
				for (int i = 0; i < stack.size(); i++) {
					TrackedEntry itr = stack.get(i);
					if (itr.type() == prev.type()) {
						if (itr.type() == ContainerType.DIMENSION) {
							if (!itr.id().equals(prev.id()) || itr.slot() != prev.slot()) {
								continue;
							}
						}
					} else continue;
					toRemove = stack.size() - i;
					for (int j = 0; j < toRemove; j++) {
						stack.pop();
					}
				}
			}
		}
		stack.push(prev);
		current = cont;
		wid = cont.containerId;
		L2Backpack.HANDLER.toClientPlayer(new AddTrackedToClient(prev, toRemove, wid), player);
	}

	public boolean serverRestore(ServerPlayer player, int wid) {
		if (stack.isEmpty()) return false;
		if (this.wid == wid) {
			LayerPopType type = stack.pop().restoreServerMenu(player);
			if (type != LayerPopType.FAIL) {
				int id = 0;
				if (player.containerMenu instanceof WorldChestContainer cont) {
					id = player.containerMenu.containerId;
				}
				this.wid = id;
				L2Backpack.HANDLER.toClientPlayer(new PopLayerToClient(type, id), player);
				return true;
			}
		}
		return false;
	}

	// --- client only values
	private boolean isWaiting;

	public void clientAddLayer(TrackedEntry entry, int toRemove, int wid) {
		for (int i = 0; i < toRemove; i++) {
			if (stack.size() > 0) {
				stack.pop();
			} else break;
		}
		this.wid = wid;
		stack.add(entry);
	}

	public void clientClear(ScreenType type) {
		isWaiting = false;
		stack.clear();
		type.perform();
	}

	private boolean onClientCloseImpl(int wid) {
		if (Screen.hasShiftDown() || isWaiting) {
			// second exit: close screen
			isWaiting = false;
			return false;
		}
		if (stack.isEmpty()) return false;
		return this.wid == wid;
	}

	public void clientPop(LayerPopType type, int wid) {
		isWaiting = false;
		if (type == LayerPopType.REMAIN) {
			stack.pop();
		} else {
			stack.clear();
		}
		this.wid = wid;
	}
}
