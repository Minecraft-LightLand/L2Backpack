package dev.xkmc.l2backpack.network;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapManager;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class SetSelectedToServer extends SerialPacketBase {

	public static final int UP = -1, DOWN = -2, SWAP = -3;

	@SerialClass.SerialField
	private int slot;

	@SerialClass.SerialField
	private boolean isAltDown;

	@Deprecated
	public SetSelectedToServer() {

	}

	@OnlyIn(Dist.CLIENT)
	public SetSelectedToServer(int slot) {
		this.slot = slot;
		isAltDown = Screen.hasAltDown();
	}

	@Override
	public void handle(NetworkEvent.Context ctx) {
		Player sender = ctx.getSender();
		if (sender == null) return;
		IQuickSwapToken token = QuickSwapManager.getToken(sender, isAltDown);
		if (token == null) return;
		if (slot == SWAP)
			token.swap(sender);
		else
			token.setSelected(slot);
	}
}
