package dev.xkmc.l2backpack.network;

import dev.xkmc.l2backpack.content.remote.DrawerAccess;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2library.network.SerialPacketBase;
import dev.xkmc.l2library.serial.SerialClass;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

@SerialClass
public class RequestTooltipUpdateEvent extends SerialPacketBase {

	@SerialClass.SerialField
	public Item item;

	@SerialClass.SerialField
	public UUID id;

	@Deprecated
	public RequestTooltipUpdateEvent() {

	}

	public RequestTooltipUpdateEvent(Item item, UUID id) {
		this.item = item;
		this.id = id;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		if (context.getSender() == null) return;
		int count = DrawerAccess.of(context.getSender().getLevel(), id, item).getCount();
		L2Backpack.HANDLER.toClientPlayer(new RespondTooltipUpdateEvent(item, id, count), context.getSender());
	}


}
