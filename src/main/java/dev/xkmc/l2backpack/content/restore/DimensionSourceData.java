package dev.xkmc.l2backpack.content.restore;

import dev.xkmc.l2library.init.events.screen.source.ItemSourceData;
import dev.xkmc.l2serial.serialization.SerialClass;

import java.util.UUID;

@SerialClass
public final class DimensionSourceData extends ItemSourceData<DimensionSourceData> {

	@SerialClass.SerialField
	private int color;
	@SerialClass.SerialField
	private int slot;
	@SerialClass.SerialField
	private UUID uuid;

	@Deprecated
	public DimensionSourceData() {

	}

	public DimensionSourceData(int color, int slot, UUID uuid) {
		this.color = color;
		this.slot = slot;
		this.uuid = uuid;
	}

	public int color() {
		return color;
	}

	public int slot() {
		return slot;
	}

	public UUID uuid() {
		return uuid;
	}

	@Override
	public boolean canReplace(DimensionSourceData other) {
		return color == other.color && slot == other.slot && uuid.equals(other.uuid);
	}
}
