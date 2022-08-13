package dev.xkmc.l2backpack.network.drawer;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class CreativeSetCarryToClient extends SerialPacketBase {

	@SerialClass.SerialField
	public Item item;

	@SerialClass.SerialField
	public int count;

	@Deprecated
	public CreativeSetCarryToClient() {

	}

	public CreativeSetCarryToClient(ItemStack stack) {
		this.item = stack.getItem();
		this.count = stack.getCount();
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		Proxy.getClientPlayer().containerMenu.setCarried(new ItemStack(item, count));
	}


}
