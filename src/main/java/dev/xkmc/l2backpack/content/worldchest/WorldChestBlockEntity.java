package dev.xkmc.l2backpack.content.worldchest;

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
public class WorldChestBlockEntity extends BaseBlockEntity implements MenuProvider, NameSetable, LidBlockEntity {

	private class Counter extends ContainerOpenersCounter {
		protected void onOpen(Level level, BlockPos pos, BlockState state) {
			level.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.ENDER_CHEST_OPEN, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
		}

		protected void onClose(Level level, BlockPos pos, BlockState state) {
			level.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.ENDER_CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
		}

		protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int i, int j) {
			level.blockEvent(WorldChestBlockEntity.this.worldPosition, state.getBlock(), 1, j);
		}

		protected boolean isOwnContainer(Player player) {
			if (player.containerMenu instanceof WorldChestContainer cont) {
				return cont.isActiveChest(WorldChestBlockEntity.this);
			} else {
				return false;
			}
		}
	}

	private final ChestLidController chestLidController = new ChestLidController();
	private final ContainerOpenersCounter openersCounter = new Counter();

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
		if (level != null && !level.isClientSide() && !this.remove &&
				cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
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

	// ender chest tile entity

	public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, WorldChestBlockEntity entity) {
		entity.chestLidController.tickLid();
	}

	public boolean triggerEvent(int i, int j) {
		if (i == 1) {
			this.chestLidController.shouldBeOpen(j > 0);
			return true;
		} else {
			return super.triggerEvent(i, j);
		}
	}

	public void startOpen(Player player) {
		if (!this.remove && !player.isSpectator()) {
			assert level != null;
			this.openersCounter.incrementOpeners(player, level, this.getBlockPos(), this.getBlockState());
		}
	}

	public void stopOpen(Player player) {
		if (!this.remove && !player.isSpectator()) {
			assert level != null;
			this.openersCounter.decrementOpeners(player, level, this.getBlockPos(), this.getBlockState());
		}
	}

	public void recheckOpen() {
		if (!this.remove) {
			assert level != null;
			this.openersCounter.recheckOpeners(level, this.getBlockPos(), this.getBlockState());
		}
	}

	public float getOpenNess(float p_59281_) {
		return this.chestLidController.getOpenness(p_59281_);
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
