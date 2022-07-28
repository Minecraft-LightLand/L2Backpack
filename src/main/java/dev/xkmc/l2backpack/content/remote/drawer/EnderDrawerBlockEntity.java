package dev.xkmc.l2backpack.content.remote.drawer;

import dev.xkmc.l2backpack.content.remote.DrawerAccess;
import dev.xkmc.l2backpack.content.remote.WorldStorage;
import dev.xkmc.l2library.base.tile.BaseBlockEntity;
import dev.xkmc.l2library.serial.SerialClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class EnderDrawerBlockEntity extends BaseBlockEntity {

	@SerialClass.SerialField
	public UUID owner_id;

	@SerialClass.SerialField
	public String owner_name;

	@SerialClass.SerialField(toClient = true)
	public Item item;

	private LazyOptional<IItemHandler> handler;

	public EnderDrawerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (level != null && !this.remove &&
				cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (level.isClientSide()) {
				return LazyOptional.of(() -> new InvWrapper(new SimpleContainer(64))).cast();
			}
			if (handler == null) {
				handler = LazyOptional.of(() -> new EnderDawerItemHandler(getAccess()));
			}
			return this.handler.cast();
		}
		return super.getCapability(cap, side);
	}

	public DrawerAccess getAccess() {
		return new DrawerAccess(WorldStorage.get((ServerLevel) level), owner_id, item);
	}


}
