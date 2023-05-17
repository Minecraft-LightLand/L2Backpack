package dev.xkmc.l2backpack.content.render;

import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class RenderEvents {

	private static final String REG_NAME = "backpack";
	public static final ModelLayerLocation BACKPACK_LAYER = new ModelLayerLocation(new ResourceLocation(L2Backpack.MODID, REG_NAME), "main");

	@SuppressWarnings({"unchecked"})
	public static void registerBackpackLayer() {
		EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
		Map<String, EntityRenderer<? extends Player>> skinMap = renderManager.getSkinMap();
		for (EntityRenderer<? extends Player> renderer : skinMap.values()) {
			if (renderer instanceof LivingEntityRenderer ler) {
				addLayer(renderManager, ler);
			}
		}
		renderManager.renderers.forEach((e, r) -> {
			if (r instanceof LivingEntityRenderer ler && ler.getModel() instanceof HumanoidModel<?>) {
				addLayer(renderManager, ler);
			}
		});
	}

	private static <T extends LivingEntity, M extends HumanoidModel<T>> void addLayer(EntityRenderDispatcher manager, LivingEntityRenderer<T, M> ler) {
		var mc = Minecraft.getInstance();
		ler.addLayer(new BackpackLayerRenderer<>(ler, mc.getEntityModels()));
		ler.addLayer(new ItemOnBackLayerRenderer<>(ler, mc.getEntityModels(), manager.getItemInHandRenderer()));
	}

}
