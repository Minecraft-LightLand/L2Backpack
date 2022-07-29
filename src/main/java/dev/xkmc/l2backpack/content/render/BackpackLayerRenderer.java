package dev.xkmc.l2backpack.content.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class BackpackLayerRenderer<T extends LivingEntity> extends RenderLayer<T, HumanoidModel<T>> {

	private static final ModelLayerLocation MLL = new ModelLayerLocation(new ResourceLocation(L2Backpack.MODID, "backpack"), "main");

	private final BackpackModel<T> model;

	public BackpackLayerRenderer(RenderLayerParent<T, HumanoidModel<T>> parent, EntityModelSet set) {
		super(parent);
		model = new BackpackModel<>(set.bakeLayer(MLL));
	}

	@Override
	public void render(PoseStack pose, MultiBufferSource buffer, int i, T entity,
					   float f0, float f1, float f2, float f3, float f4, float f5) {
		this.getParentModel().copyPropertiesTo(model);
		model.setupAnim(entity, f0, f1, f3, f4, f5);
		ResourceLocation texture = new ResourceLocation(L2Backpack.MODID, "textures/backpack/white.png");
		VertexConsumer vc = buffer.getBuffer(RenderType.armorCutoutNoCull(texture));
		this.model.body.getChild("main_body").render(pose, vc, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
	}
}
