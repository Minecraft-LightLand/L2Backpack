package dev.xkmc.l2backpack.network;

import dev.xkmc.l2backpack.events.TooltipUpdateEvents;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class RespondTooltipUpdateEvent extends SerialPacketBase {

	@SerialClass.SerialField
	public Item item;

	@SerialClass.SerialField
	public int count;

	@Deprecated
	public RespondTooltipUpdateEvent() {

	}

	public RespondTooltipUpdateEvent(Item item, int count) {
		this.item = item;
		this.count = count;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		TooltipUpdateEvents.updateInfo(item, count);
	}

}
