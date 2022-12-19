package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.content.remote.common.AnalogTrigger;
import dev.xkmc.l2library.block.impl.BlockEntityBlockMethodImpl;
import dev.xkmc.l2library.repack.registrate.util.entry.BlockEntityEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WorldChestAnalogBlockEntity<T extends BlockEntity> extends BlockEntityBlockMethodImpl<T> {

	public WorldChestAnalogBlockEntity(BlockEntityEntry<T> type, Class<T> cls) {
		super(type, cls);
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		BlockEntity e = worldIn.getBlockEntity(pos);
		if (e instanceof WorldChestBlockEntity be) {
			AnalogTrigger.trigger(worldIn, be.owner_id);
		}
		return super.getAnalogOutputSignal(blockState, worldIn, pos);
	}

}
