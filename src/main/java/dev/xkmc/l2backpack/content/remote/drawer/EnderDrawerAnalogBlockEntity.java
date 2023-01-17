package dev.xkmc.l2backpack.content.remote.drawer;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.xkmc.l2backpack.content.remote.common.AnalogTrigger;
import dev.xkmc.l2backpack.content.remote.common.DrawerAccess;
import dev.xkmc.l2library.block.impl.BlockEntityBlockMethodImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EnderDrawerAnalogBlockEntity<T extends BlockEntity> extends BlockEntityBlockMethodImpl<T> {

	public EnderDrawerAnalogBlockEntity(BlockEntityEntry<T> type, Class<T> cls) {
		super(type, cls);
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		if (worldIn.isClientSide()) return 0;
		BlockEntity e = worldIn.getBlockEntity(pos);
		if (e instanceof EnderDrawerBlockEntity be) {
			DrawerAccess access = be.getAccess();
			int max = access.item().getMaxStackSize() * 64;
			int count = access.getCount();
			AnalogTrigger.trigger(worldIn, be.owner_id);
			return (int) (Math.floor(14.0 * count / max) + (count > 0 ? 1 : 0));
		}
		return super.getAnalogOutputSignal(blockState, worldIn, pos);
	}

}
