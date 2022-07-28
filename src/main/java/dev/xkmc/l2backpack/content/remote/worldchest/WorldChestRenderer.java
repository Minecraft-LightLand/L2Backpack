package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldChestRenderer extends ChestRenderer<WorldChestBlockEntity> {

	public static final ResourceLocation CHEST_SHEET = new ResourceLocation("textures/atlas/chest.png");

	public WorldChestRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected Material getMaterial(WorldChestBlockEntity blockEntity, ChestType chestType) {
		String color = blockEntity.getBlockState().getValue(WorldChestBlock.COLOR).getName();
		return new Material(CHEST_SHEET, new ResourceLocation(L2Backpack.MODID, "entity/" + color));
	}
}
