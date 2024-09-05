package dev.xkmc.l2backpack.content.remote.dimensional;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupMode;
import dev.xkmc.l2backpack.content.remote.common.LBSavedData;
import dev.xkmc.l2backpack.content.remote.common.StorageContainer;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2core.base.tile.BaseBlockEntity;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

@SerialClass
public class DimensionalBlockEntity extends BaseBlockEntity implements MenuProvider, Nameable, ContainerListener {

	@SerialField
	public UUID ownerId;
	@SerialField
	public Component ownerName;
	@SerialField
	long password;
	@SerialField
	public int color;
	@SerialField
	public PickupConfig config = PickupConfig.DEF;

	protected Component name;

	public DimensionalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Nullable
	public IItemHandler getItemHandler() {
		if (level == null || remove) return null;
		if (!(level instanceof ServerLevel sl)) {
			return new InvWrapper(new SimpleContainer(27));
		}
		Optional<StorageContainer> storage = LBSavedData.get(sl).getOrCreateStorage(ownerId, color, password, null, null, 0);
		if (storage.isEmpty()) return null;

		if (config == null || config.pickup() == PickupMode.NONE) {
			return new DimensionalInvWrapper(storage.get().get(), ownerId);
		} else {
			return new BlockPickupInvWrapper(sl, this, storage.get(), config);
		}
	}

	public void setColor(int color) {
		if (this.color == color)
			return;
		this.color = color;
		this.password = color;
		this.setChanged();
	}

	@Override
	public Component getName() {
		if (name != null) return name;
		return ownerName;
	}

	@Override
	public Component getDisplayName() {
		return getName();
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int wid, Inventory inventory, Player player) {
		if (level == null || ownerId == null) return null;
		Optional<StorageContainer> storage = getAccess();
		if (storage.isEmpty()) return null;
		return new DimensionalContainer(wid, inventory, storage.get().get(), storage.get(), this);
	}

	public boolean stillValid(Player player) {
		assert level != null;
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
		}
	}

	private Optional<StorageContainer> getAccess() {
		assert level != null;
		return LBSavedData.get((ServerLevel) level).getOrCreateStorage(ownerId, color, password, null, null, 0);
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
		if (!added && level != null && !level.isClientSide() && ownerId != null) {
			added = true;
			getAccess().ifPresent(e -> e.get().addListener(this));
		}
	}

	public void removeFromListener() {
		if (added && level != null && !level.isClientSide() && ownerId != null) {
			added = false;
			getAccess().ifPresent(e -> e.get().removeListener(this));
		}
	}

	@Override
	protected void applyImplicitComponents(DataComponentInput data) {
		super.applyImplicitComponents(data);
		name = data.get(DataComponents.CUSTOM_NAME);
		config = data.getOrDefault(LBItems.DC_PICKUP, PickupConfig.DEF);
		ownerId = data.getOrDefault(LBItems.DC_OWNER_ID, Util.NIL_UUID);
		ownerName = data.getOrDefault(LBItems.DC_OWNER_NAME, Component.empty());
		password = data.getOrDefault(LBItems.DC_PASSWORD, 0L);
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder data) {
		super.collectImplicitComponents(data);
		data.set(LBItems.DC_PICKUP, config);
		data.set(LBItems.DC_OWNER_ID, ownerId);
		data.set(LBItems.DC_OWNER_NAME, ownerName);
		data.set(LBItems.DC_PASSWORD, password);
		data.set(DataComponents.CUSTOM_NAME, name);
	}

	@Override
	public void containerChanged(Container p_18983_) {
		setChanged();
	}

	public void setPickupMode(PickupConfig click) {
		this.config = click;
		if (level != null) level.invalidateCapabilities(getBlockPos());
		sync();
		setChanged();
	}

}
