
package dev.xkmc.l2backpack.content.drawer;

import dev.xkmc.l2library.serial.SerialClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SerialClass
public class DrawerBlockEntity extends IDrawerBlockEntity {

	@SerialClass.SerialField(toClient = true)
	public final DrawerHandler handler = new DrawerHandler(this);

	public DrawerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			return LazyOptional.of(() -> handler).cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public Item getItem() {
		return handler.item;
	}
}
