package dev.xkmc.l2backpack.content.render;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xkmc.l2backpack.content.common.BackpackModelItem;
import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.drawer.DrawerItem;
import dev.xkmc.l2backpack.init.registrate.BackpackBlocks;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;

import java.util.function.Supplier;

public class BaseItemRenderer extends BlockEntityWithoutLevelRenderer {

	public static final Supplier<BlockEntityWithoutLevelRenderer> INSTANCE = Suppliers.memoize(() -> new BaseItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()));

	public static final IClientItemExtensions EXTENSIONS = new IClientItemExtensions() {

		@Override
		public BlockEntityWithoutLevelRenderer getCustomRenderer() {
			return INSTANCE.get();
		}

	};

	private static final RandomSource random = RandomSource.create(42);

	private final BackpackModel<LivingEntity> model;

	public BaseItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet set) {
		super(dispatcher, set);
		model = new BackpackModel<>(set.bakeLayer(BackpackLayerRenderer.MLL));
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext type, PoseStack pose,
							 MultiBufferSource bufferSource, int light, int overlay) {
		if (stack.getItem() instanceof BaseDrawerItem) {
			renderDrawer(stack, type, pose, bufferSource, light, overlay);
		}
		if (stack.getItem() instanceof BackpackModelItem item) {
			pose.pushPose();
			transformBackpack(pose, type);
			renderBackpack(item, stack, bufferSource, pose, light);
			pose.popPose();
		}
	}

	private void transformBackpack(PoseStack pose, ItemDisplayContext type) {
		switch (type) {
			case GUI:
				break;
			case FIRST_PERSON_LEFT_HAND:{
				pose.translate(0.3, 0.9, 0.6);
				float size = 0.3f;
				pose.scale(size, size, size);
				pose.mulPose(Axis.ZP.rotationDegrees(180));
				pose.mulPose(Axis.XP.rotationDegrees(-10));
				pose.mulPose(Axis.YP.rotationDegrees(-250));
				return;
			}
			case FIRST_PERSON_RIGHT_HAND:{
				pose.translate(0.7, 0.9, 0.6);
				float size = 0.3f;
				pose.scale(size, size, size);
				pose.mulPose(Axis.ZP.rotationDegrees(180));
				pose.mulPose(Axis.XP.rotationDegrees(-10));
				pose.mulPose(Axis.YP.rotationDegrees(250));
				return;
			}
			case THIRD_PERSON_LEFT_HAND:
			case THIRD_PERSON_RIGHT_HAND: {
				pose.translate(0.25, 0.4, 0.5);
				float size = 0.625f;
				pose.scale(size, size, size);
				break;
			}
			case GROUND: {
				pose.translate(0.8, 0, 0.6);
				float size = 0.6f;
				pose.scale(size, size, size);
				pose.mulPose(Axis.XP.rotationDegrees(30));
				pose.mulPose(Axis.YP.rotationDegrees(-180));
				break;
			}
			case NONE:
			case HEAD:
			case FIXED: {
				pose.translate(0.5, 0.5, 0.5);
				float size = 0.5f;
				pose.scale(size, -size, size);
				pose.translate(0, -0.5, 0);
				return;
			}
		}
		pose.mulPose(Axis.ZP.rotationDegrees(180));
		pose.mulPose(Axis.XP.rotationDegrees(-30));
		pose.mulPose(Axis.YP.rotationDegrees(225));
		float size = 0.65f;
		pose.scale(size, size, size);
		pose.translate(0.8, -1.75, 0.7);
	}

	private void renderBackpack(BackpackModelItem item, ItemStack stack, MultiBufferSource bufferSource,
								PoseStack pose, int light) {
		ResourceLocation texture = item.getModelTexture(stack);
		VertexConsumer vc = bufferSource.getBuffer(RenderType.armorCutoutNoCull(texture));
		this.model.body.getChild("main_body")
				.render(pose, vc, light, OverlayTexture.NO_OVERLAY,
						1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void renderDrawer(ItemStack stack, ItemDisplayContext type, PoseStack poseStack,
									MultiBufferSource bufferSource, int light, int overlay) {
		poseStack.popPose();
		poseStack.pushPose();

		poseStack.pushPose();
		BlockState state = BackpackBlocks.ENDER_DRAWER.getDefaultState();
		if (stack.getItem() instanceof DrawerItem) {
			state = BackpackBlocks.DRAWER.getDefaultState();
		}
		BakedModel model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state);
		model = model.applyTransform(type, poseStack, false);
		poseStack.translate(-.5F, -.5F, -.5F);

		ModelBlockRenderer renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
		PoseStack.Pose pose = poseStack.last();

		random.setSeed(42);
		for (RenderType rt : model.getRenderTypes(state, random, ModelData.EMPTY)) {
			renderer.renderModel(pose, bufferSource.getBuffer(ForgeHooksClient.getEntityRenderType(rt, false)),
					state, model, 1F, 1F, 1F, light, overlay, ModelData.EMPTY, rt);
		}

		Item item = BaseDrawerItem.getItem(stack);
		int count = stack.getItem() instanceof DrawerItem ? DrawerItem.getCount(stack) : 1;
		ItemStack inv = new ItemStack(item, count);
		renderItemInside(inv, item instanceof BlockItem ? 0.5D : 0.625D, poseStack, type, bufferSource, light, overlay);

		poseStack.popPose();
	}


	public static void renderItemInside(ItemStack stack, double height, PoseStack matrix, ItemDisplayContext type,
										MultiBufferSource buffer, int light, int overlay) {
		var mc = Minecraft.getInstance();
		float time = (mc.getPartialTick() + Proxy.getPlayer().tickCount) % 80;
		if (!stack.isEmpty()) {
			matrix.pushPose();
			matrix.translate(0.5D, height, 0.5D);
			matrix.scale(2f, 2f, 2f);
			matrix.translate(0, -0.2f, 0);
			matrix.mulPose(Axis.YP.rotationDegrees(time * 4.5F));
			var r = Minecraft.getInstance().getItemRenderer();
			//if (r.getModel(stack, null, null, 0).usesBlockLight())
			Lighting.setupForFlatItems();
			r.renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, matrix, buffer, null, 0);
			Lighting.setupFor3DItems();
			matrix.popPose();
		}

	}

}
