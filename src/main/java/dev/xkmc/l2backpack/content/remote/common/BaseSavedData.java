package dev.xkmc.l2backpack.content.remote.common;

import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

@SerialClass
public class BaseSavedData<T extends BaseSavedData<T>> extends SavedData {

	protected BaseSavedData() {

	}

	protected BaseSavedData(Class<T> cls, CompoundTag data) {
		TagCodec.fromTag(data, cls, this, e -> true);
	}

	@Override
	public boolean isDirty() {
		return true;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		TagCodec.toTag(tag, this);
		return tag;
	}

}
