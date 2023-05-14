package dev.xkmc.l2backpack.content.remote.drawer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.xkmc.l2backpack.content.drawer.IDrawerBlockEntity;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class DrawerRenderer implements BlockEntityRenderer<IDrawerBlockEntity> {

	public DrawerRenderer(BlockEntityRendererProvider.Context context) {

	}

	@Override
	public void render(IDrawerBlockEntity entity, float pTick, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
		var mc = Minecraft.getInstance();
		float time = (mc.getPartialTick() + Proxy.getPlayer().tickCount) % 80;
		ItemStack stack = new ItemStack(entity.getItem(), 1);
		if (!stack.isEmpty()) {
			pose.pushPose();
			pose.translate(0.5D, entity.getItem() instanceof BlockItem ? 0.5D : 0.625D, 0.5D);
			pose.scale(2f, 2f, 2f);
			pose.translate(0, -0.2f, 0);
			pose.mulPose(Vector3f.YP.rotationDegrees(time * 4.5F));
			Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, light, overlay, pose, buffer, 0);
			pose.popPose();
		}
	}

}
