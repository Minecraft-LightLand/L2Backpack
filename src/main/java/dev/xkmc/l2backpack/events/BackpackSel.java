package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapOverlay;
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
		if (L2Keys.WHEEL.map.isDown() || WheelHandler.wheel instanceof IQuickSwapToken.SwapWheel) {
			return !QuickSwapManager.getWheelTokens(player, L2Keys.hasShiftDown()).isEmpty();
		}
		IQuickSwapToken<?> token = QuickSwapManager.getToken(player, QuickSwapOverlay.hasAltDown());
		return token != null;
	}

	@Override
	public void handleServerSetSelection(SetSelectedToServer packet, Player player) {
		IQuickSwapToken<?> token = QuickSwapManager.getToken(player, packet.isAltDown());
		if (token == null) return;
		if (packet.slot() == SWAP)
			token.swap(player);
		else
			token.setSelected(packet.slot());
	}

	@Override
	public boolean handleClientScroll(int i, Player player) {
		if (LBConfig.CLIENT.reverseScroll.get()) {
			i = -i;
		}
		if (i > 0) {
			toServer(UP);
		} else if (i < 0) {
			toServer(DOWN);
		}
		return true;
	}

	@Override
	public boolean handleClientScroll(int diff, double delta, Player player) {
		if (delta == 0) return true;
		return handleClientScroll(delta > 0 ? 1 : -1, player);
	}

	@Override
	public void handleClientKey(L2Keys key, Player player) {
		if (!QuickSwapOverlay.INSTANCE.isScreenOn()) return;
		if (key == L2Keys.SWAP) {
			toServer(SWAP);
		} else if (key == L2Keys.UP) {
			toServer(UP);
		} else if (key == L2Keys.DOWN) {
			toServer(DOWN);
		}
	}

	@Override
	public boolean handleClientNumericKey(int i, BooleanSupplier click) {
		if (!QuickSwapOverlay.INSTANCE.isOnHold()) return false;
		if (click.getAsBoolean()) {
			toServer(i);
			return true;
		}
		return false;
	}

	@Override
	public boolean isHoldKeyDown(Player player) {
		return QuickSwapOverlay.INSTANCE.isOnHold();
	}

	private int prevWheel = 0;
	private QuickSwapType prevType = null;

	@Override
	public Optional<WheelAdaptor> get(@Nullable Player player, int wheel) {
		if (player == null) return Optional.empty();
		var list = QuickSwapManager.getWheelTokens(player, L2Keys.hasShiftDown());
		list.removeIf(e -> !e.type().supportWheel());
		if (list.isEmpty()) return Optional.empty();
		int index = list.size() == 1 ? 0 : wheel % list.size();
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
		return token.get(player, index, list.size() == 1 ? ItemStack.EMPTY : list.get((index + 1) % list.size()).stack());
	}

}
