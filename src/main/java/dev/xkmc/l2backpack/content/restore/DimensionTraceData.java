package dev.xkmc.l2backpack.content.restore;

import dev.xkmc.l2library.init.events.screen.track.TrackedEntryData;
import dev.xkmc.l2serial.serialization.SerialClass;

import java.util.UUID;

@SerialClass
public final class DimensionTraceData extends TrackedEntryData<DimensionTraceData> {

	@SerialClass.SerialField
	private int color;

	@SerialClass.SerialField
	private UUID uuid;

	@Deprecated
	public DimensionTraceData() {
	}

	public DimensionTraceData(int color, UUID uuid) {
		this.color = color;
		this.uuid = uuid;
	}

	public int color() {
		return color;
	}

	public UUID uuid() {
		return uuid;
	}

}