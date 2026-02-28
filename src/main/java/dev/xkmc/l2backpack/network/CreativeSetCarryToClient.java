package dev.xkmc.l2backpack.network;

import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class CreativeSetCarryToClient extends SerialPacketBase {

	@SerialClass.SerialField
	public ItemStack item;

	@Deprecated
	public CreativeSetCarryToClient() {

	}

	public CreativeSetCarryToClient(ItemStack stack) {
		this.item = stack;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		Proxy.getClientPlayer().containerMenu.setCarried(item);
	}


}
