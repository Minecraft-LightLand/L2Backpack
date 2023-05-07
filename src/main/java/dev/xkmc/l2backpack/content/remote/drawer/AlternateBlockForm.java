package dev.xkmc.l2backpack.content.remote.drawer;

import dev.xkmc.l2modularblock.mult.CreateBlockStateBlockMethod;
import dev.xkmc.l2modularblock.mult.DefaultStateBlockMethod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.ApiStatus;

//FIXME remove
@Deprecated
@ApiStatus.ScheduledForRemoval
public class AlternateBlockForm implements CreateBlockStateBlockMethod, DefaultStateBlockMethod {

	public static final BooleanProperty ALT = BlockStateProperties.INVERTED;

	public static final AlternateBlockForm INSTANCE = new AlternateBlockForm();

	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ALT);
	}

	@Override
	public BlockState getDefaultState(BlockState blockState) {
		return blockState.setValue(ALT, false);
	}
}
