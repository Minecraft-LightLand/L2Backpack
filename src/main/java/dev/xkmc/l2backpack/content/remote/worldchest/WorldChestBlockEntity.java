package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.content.remote.StorageContainer;
import dev.xkmc.l2backpack.content.remote.WorldStorage;
import dev.xkmc.l2backpack.init.data.LangData;
import dev.xkmc.l2library.base.tile.BaseBlockEntity;
import dev.xkmc.l2library.block.NameSetable;
import dev.xkmc.l2library.serial.SerialClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

@SerialClass
public class WorldChestBlockEntity extends BaseBlockEntity implements MenuProvider, NameSetable {

	@SerialClass.SerialField
	public UUID owner_id;
	@SerialClass.SerialField(toClient = true)
	public String owner_name;
	@SerialClass.SerialField
	long password;
	@SerialClass.SerialField(toClient = true)
	private int color;

	private Component name;

	private LazyOptional<IItemHandler> handler;

	public WorldChestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (level != null && !this.remove &&
				cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (level.isClientSide()) {
				return LazyOptional.of(() -> new InvWrapper(new SimpleContainer(27))).cast();
			}
			if (handler == null) {
				Optional<StorageContainer> storage = WorldStorage.get((ServerLevel) level).getOrCreateStorage(owner_id, color, password);
				handler = storage.isEmpty() ? LazyOptional.empty() : LazyOptional.of(() -> new InvWrapper(storage.get().container));
			}
			return this.handler.cast();
		}
		return super.getCapability(cap, side);
	}

	public void setColor(int color) {
		if (this.color == color)
			return;
		handler = null;
		this.color = color;
		this.password = color;
		this.setChanged();
	}

	@Override
	public Component getName() {
		return name == null ? LangData.IDS.STORAGE_OWNER.get(owner_name) : name;
	}

	@Override
	public Component getDisplayName() {
		return getName();
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int wid, Inventory inventory, Player player) {
		if (level == null || owner_id == null) return null;
		Optional<StorageContainer> storage = WorldStorage.get((ServerLevel) level).getOrCreateStorage(owner_id, color, password);
		if (storage.isEmpty()) return null;
		return new WorldChestContainer(wid, inventory, storage.get().container, storage.get(), getName(), this);
	}

	@Override
	public void setCustomName(Component component) {
		name = component;
	}

	public boolean stillValid(Player player) {
		assert level != null;
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
		}
	}

}
