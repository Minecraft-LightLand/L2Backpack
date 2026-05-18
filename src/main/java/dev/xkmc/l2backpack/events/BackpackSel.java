package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapManager;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.LBConfig;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.overlay.WheelAdaptor;
import dev.xkmc.l2itemselector.overlay.WheelHandler;
import dev.xkmc.l2itemselector.select.ISelectionListener;
import dev.xkmc.l2itemselector.select.SetSelectedToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BooleanSupplier;

public class BackpackSel implements ISelectionListener, WheelAdaptor.Provider {

	public static final BackpackSel INSTANCE = new BackpackSel();

	public static final int UP = -1, DOWN = -2, SWAP = -3;

	private static final ResourceLocation ID = L2Backpack.loc("backpack");

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean isClientActive(Player player) {
		if (Minecraft.getInstance().screen != null) return false;
		return L2Keys.WHEEL.map.isDown() || WheelHandler.wheel instanceof IQuickSwapToken.SwapWheel
				|| WheelHandler.wheelPressTime >= 0;
	}

	@Override
	public void handleServerSetSelection(SetSelectedToServer packet, Player player) {
		var list = QuickSwapManager.getWheelTokens(player, packet.isShiftDown());
		if (packet.slot() == SWAP) {
			for (var token : list) {
				token.swap(player);
			}
		} else {
			for (var token : list) {
				token.setSelected(packet.slot());
			}
		}
	}

	@Override
	public boolean handleClientScroll(int diff, Player player) {
		if (WheelHandler.wheel != null) {
			int current = WheelHandler.keyboardIndex >= 0 ? WheelHandler.keyboardIndex : WheelHandler.wheel.getIndex(player);
			int total = WheelHandler.wheel.getWheelSize();
			if (LBConfig.CLIENT.reverseScroll.get()) diff = -diff;
			WheelHandler.keyboardIndex = Math.floorMod(current - diff, total);
			return true;
		}
		return false;
	}

	@Override
	public boolean handleClientScroll(int diff, double delta, Player player) {
		if (delta == 0) return true;
		return handleClientScroll(delta > 0 ? 1 : -1, player);
	}

	@Override
	public boolean scrollBypassShift() {
		return WheelHandler.wheel != null;
	}

	@Override
	public void handleClientKey(L2Keys key, Player player) {
		if (WheelHandler.wheel != null) {
			int current = WheelHandler.keyboardIndex >= 0 ? WheelHandler.keyboardIndex : WheelHandler.wheel.getIndex(player);
			int total = WheelHandler.wheel.getWheelSize();
			if (key == L2Keys.UP) {
				WheelHandler.keyboardIndex = Math.floorMod(current - 1, total);
			} else if (key == L2Keys.DOWN) {
				WheelHandler.keyboardIndex = Math.floorMod(current + 1, total);
			} else if (key == L2Keys.LEFT) {
				int target = WheelHandler.wheelIndex - 1;
				if (WheelAdaptor.get(player, target) != null) {
					WheelHandler.wheelIndex = target;
				}
			} else if (key == L2Keys.RIGHT) {
				int target = WheelHandler.wheelIndex + 1;
				if (WheelAdaptor.get(player, target) != null) {
					WheelHandler.wheelIndex = target;
				}
			}
		}
	}

	@Override
	public boolean handleClientNumericKey(int i, BooleanSupplier click) {
		return false;
	}

	@Override
	public boolean isHoldKeyDown(Player player) {
		return false;
	}

	private int prevWheel = 0;
	private QuickSwapType prevType = null;

	@Override
	public Optional<WheelAdaptor> get(@Nullable Player player, int wheel) {
		if (player == null) return Optional.empty();
		var list = QuickSwapManager.getWheelTokens(player, L2Keys.hasShiftDown());
		list.removeIf(e -> !e.type().supportWheel());
		if (list.isEmpty()) return Optional.empty();
		int index;
		if (list.size() == 1) {
			if (wheel != 0) return Optional.empty();
			index = 0;
		} else {
			index = Math.floorMod(wheel, list.size());
		}
		var token = list.get(index);
		if (prevType != null && prevWheel > 0 && prevWheel == wheel && prevType != token.type()) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).type() == prevType) {
					WheelHandler.wheelIndex = wheel = index = i;
					token = list.get(i);
					break;
				}
			}
		}
		prevWheel = wheel;
		prevType = token.type();
		ItemStack prevStack = list.size() == 1 ? ItemStack.EMPTY : list.get(Math.floorMod(index - 1, list.size())).stack();
		ItemStack nextStack = list.size() == 1 ? ItemStack.EMPTY : list.get((index + 1) % list.size()).stack();
		return token.get(player, index, prevStack, nextStack);
	}

}
