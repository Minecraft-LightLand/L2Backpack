package dev.xkmc.l2backpack.network;

import dev.xkmc.l2backpack.content.quickswap.quiver.ArrowBag;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapManager;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class SetArrowToServer extends SerialPacketBase {

	@SerialClass.SerialField
	private int slot;

	@Deprecated
	public SetArrowToServer() {

	}

	public SetArrowToServer(int slot) {
		this.slot = slot;
	}

	@Override
	public void handle(NetworkEvent.Context ctx) {
		Player sender = ctx.getSender();
		if (sender == null) return;
		ItemStack bag = QuickSwapManager.getArrowBag(sender);
		if (bag.isEmpty()) return;
		ArrowBag.setSelected(bag, slot);
	}
}
