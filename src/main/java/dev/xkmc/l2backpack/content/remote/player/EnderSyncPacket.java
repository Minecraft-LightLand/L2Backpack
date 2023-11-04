package dev.xkmc.l2backpack.content.remote.player;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.events.TooltipUpdateEvents;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;

@SerialClass
public class EnderSyncPacket extends SerialPacketBase {

	@SerialClass.SerialField
	public ArrayList<Entry> list = new ArrayList<>();

	@Deprecated
	public EnderSyncPacket() {

	}

	public EnderSyncPacket(List<Pair<Integer, ItemStack>> list) {
		for (var e : list) {
			this.list.add(new Entry(e.getFirst(), e.getSecond()));
		}
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		for (var e : list) {
			TooltipUpdateEvents.onEnderSync(e.slot(), e.stack());
		}
	}

	public record Entry(int slot, ItemStack stack) {

	}

}
