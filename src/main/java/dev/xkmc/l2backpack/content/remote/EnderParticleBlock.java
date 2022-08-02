package dev.xkmc.l2backpack.content.remote;

import dev.xkmc.l2library.block.mult.AnimateTickBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class EnderParticleBlock implements AnimateTickBlockMethod {

	public static EnderParticleBlock INSTANCE = new EnderParticleBlock();

	public void animateTick(BlockState state, Level level, BlockPos pos, Random source) {
		for (int i = 0; i < 3; ++i) {
			int j = source.nextInt(2) * 2 - 1;
			int k = source.nextInt(2) * 2 - 1;
			double d0 = pos.getX() + 0.5D + 0.25D * j;
			double d1 = pos.getY() + source.nextFloat();
			double d2 = pos.getZ() + 0.5D + 0.25D * k;
			double d3 = source.nextFloat() * j;
			double d4 = (source.nextFloat() - 0.5D) * 0.125D;
			double d5 = source.nextFloat() * k;
			level.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
		}

	}

}
