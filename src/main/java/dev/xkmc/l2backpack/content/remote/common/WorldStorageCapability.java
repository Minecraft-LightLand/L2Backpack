package dev.xkmc.l2backpack.content.remote.common;

import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SerialClass
public class WorldStorageCapability implements ICapabilitySerializable<CompoundTag> {

	public final ServerLevel w;
	public final WorldStorage handler;
	public final LazyOptional<WorldStorage> lo;

	public WorldStorageCapability(ServerLevel level) {
		this.w = level;
		handler = new WorldStorage(level);
		lo = LazyOptional.of(() -> this.handler);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction direction) {
		if (capability == WorldStorage.CAPABILITY)
			return lo.cast();
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		return TagCodec.toTag(new CompoundTag(), lo.resolve().get());
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		Wrappers.get(() -> TagCodec.fromTag(tag, WorldStorage.class, handler, f -> true));
		handler.init();
	}

}
