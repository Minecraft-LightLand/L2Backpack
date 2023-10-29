package dev.xkmc.l2backpack.network;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class CreativeSetCarryToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public ItemStack stack;

	@SerialClass.SerialField
	public int count;

	@Deprecated
	public CreativeSetCarryToServer() {

	}

	public CreativeSetCarryToServer(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var player = context.getSender();
		if (player == null) return;
		if (!player.getAbilities().instabuild) return;
		player.containerMenu.setCarried(stack);
	}


}
