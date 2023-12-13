package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapOverlay;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapManager;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.BackpackConfig;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.select.ISelectionListener;
import dev.xkmc.l2itemselector.select.SetSelectedToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.BooleanSupplier;

public class BackpackSel implements ISelectionListener {

	public static final BackpackSel INSTANCE = new BackpackSel();

	public static final int UP = -1, DOWN = -2, SWAP = -3;

	private static final ResourceLocation ID = new ResourceLocation(L2Backpack.MODID, "backpack");

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean isClientActive(Player player) {
		if (Minecraft.getInstance().screen != null) return false;
		IQuickSwapToken token = QuickSwapManager.getToken(player, Screen.hasAltDown());
		return token != null;
	}

	@Override
	public void handleServerSetSelection(SetSelectedToServer packet, Player player) {
		IQuickSwapToken token = QuickSwapManager.getToken(player, packet.isAltDown);
		if (token == null) return;
		if (packet.slot == SWAP)
			token.swap(player);
		else
			token.setSelected(packet.slot);
	}

	@Override
	public boolean handleClientScroll(int i, Player player) {
		if (BackpackConfig.CLIENT.reverseScroll.get()) {
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
		if (!Minecraft.getInstance().options.keyShift.isDown()) return false;
		if (click.getAsBoolean()) {
			toServer(i);
			return true;
		}
		return false;
	}
}
