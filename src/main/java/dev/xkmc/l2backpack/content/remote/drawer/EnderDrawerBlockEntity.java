package dev.xkmc.l2backpack.content.remote.drawer;

import dev.xkmc.l2backpack.content.drawer.IDrawerBlockEntity;
import dev.xkmc.l2backpack.content.remote.common.DrawerAccess;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SerialClass
public class EnderDrawerBlockEntity extends IDrawerBlockEntity {

	@SerialClass.SerialField(toClient = true)
	public UUID owner_id;

	@SerialClass.SerialField(toClient = true)
	public String owner_name;

	@SerialClass.SerialField(toClient = true)
	public Item item;

	@SerialClass.SerialField(toClient = true)
	public CompoundTag config;

	private LazyOptional<IItemHandler> handler;

	public EnderDrawerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (level != null && !this.remove &&
				cap == ForgeCapabilities.ITEM_HANDLER) {
			if (level.isClientSide()) {
				return LazyOptional.of(() -> new InvWrapper(new SimpleContainer(64))).cast();
			}
			if (handler == null) {
				handler = owner_id == null ? LazyOptional.empty() : LazyOptional.of(() -> new EnderDawerItemHandler(getAccess(), true));
			}
			return this.handler.cast();
		}
		return super.getCapability(cap, side);
	}

	public DrawerAccess getAccess() {
		return DrawerAccess.of(level, owner_id, item);
	}

	private boolean added = false;

	@Override
	public void onChunkUnloaded() {
		removeFromListener();
		super.onChunkUnloaded();
	}

	@Override
	public void setRemoved() {
		removeFromListener();
		super.setRemoved();
	}

	@Override
	public void onLoad() {
		super.onLoad();
		addToListener();
	}

	public void addToListener() {
		if (!added && level != null && !level.isClientSide() && owner_id != null) {
			added = true;
			getAccess().listener.add(this);
		}
	}

	public void removeFromListener() {
		if (added && level != null && !level.isClientSide() && owner_id != null) {
			added = false;
			getAccess().listener.remove(this);
		}
	}

	@Override
	public Item getItem() {
		return item;
	}
}
