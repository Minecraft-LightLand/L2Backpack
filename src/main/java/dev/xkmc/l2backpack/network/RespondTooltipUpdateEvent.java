package dev.xkmc.l2backpack.network;

import dev.xkmc.l2backpack.events.TooltipUpdateEvents;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

@SerialClass
public class RespondTooltipUpdateEvent extends SerialPacketBase {

	@SerialClass.SerialField
	public Item item;

	@SerialClass.SerialField
	public UUID id;

	@SerialClass.SerialField
	public int count;

	@Deprecated
	public RespondTooltipUpdateEvent() {

	}

	public RespondTooltipUpdateEvent(Item item, UUID id, int count) {
		this.item = item;
		this.id = id;
		this.count = count;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		TooltipUpdateEvents.updateInfo(item, id, count);
	}

}
