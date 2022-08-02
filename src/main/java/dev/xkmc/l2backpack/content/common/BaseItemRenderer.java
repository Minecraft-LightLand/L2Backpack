package dev.xkmc.l2backpack.content.common;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.drawer.DrawerItem;
import dev.xkmc.l2backpack.content.remote.drawer.AlternateBlockForm;
import dev.xkmc.l2backpack.init.registrate.BackpackBlocks;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Random;
import java.util.function.Supplier;

public class BaseItemRenderer extends BlockEntityWithoutLevelRenderer {

	public static final Supplier<BlockEntityWithoutLevelRenderer> INSTANCE = Suppliers.memoize(() -> new BaseItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()));

	public static final IItemRenderProperties EXTENSIONS = new IItemRenderProperties() {

		@Override
		public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
			return INSTANCE.get();
		}

	};

	private static final Random RANDOM = new Random(42);


	public BaseItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet set) {
		super(dispatcher, set);
	}

	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType type, PoseStack poseStack,
							 MultiBufferSource bufferSource, int light, int overlay) {

		poseStack.popPose();
		poseStack.pushPose();

		poseStack.pushPose();
		BlockState state = BackpackBlocks.ENDER_DRAWER.getDefaultState();
		if (stack.getItem() instanceof DrawerItem) {
			state = state.setValue(AlternateBlockForm.ALT, true);
		}
		BakedModel model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state);
		model = model.handlePerspective(type, poseStack);
		poseStack.translate(-.5F, -.5F, -.5F);

		ModelBlockRenderer renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
		PoseStack.Pose pose = poseStack.last();
		VertexConsumer vc = bufferSource.getBuffer(Sheets.cutoutBlockSheet());
		RANDOM.setSeed(42L);
		for (BakedQuad rt : model.getQuads(state, null, RANDOM, EmptyModelData.INSTANCE)) {
			vc.putBulkData(pose, rt, 1, 1, 1, light, overlay, true);
		}

		Item item = BaseDrawerItem.getItem(stack);
		int count = stack.getItem() instanceof DrawerItem ? DrawerItem.getCount(stack) : 1;
		ItemStack inv = new ItemStack(item, count);
		renderItemInside(inv, item instanceof BlockItem ? 0.5D : 0.625D, poseStack, type, bufferSource, light, overlay);

		poseStack.popPose();
	}


	public static void renderItemInside(ItemStack stack, double height, PoseStack matrix, ItemTransforms.TransformType type, MultiBufferSource buffer, int light, int overlay) {
		var mc = Minecraft.getInstance();
		float time = (mc.getFrameTime() + Proxy.getPlayer().tickCount) % 80;
		if (!stack.isEmpty()) {
			matrix.pushPose();
			matrix.translate(0.5D, height, 0.5D);
			matrix.scale(2f, 2f, 2f);
			matrix.translate(0, -0.2f, 0);
			matrix.mulPose(Vector3f.YP.rotationDegrees(time * 4.5F));
			Lighting.setupForFlatItems();
			Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, light, overlay, matrix, buffer, 0);
			matrix.popPose();
		}

	}
}
