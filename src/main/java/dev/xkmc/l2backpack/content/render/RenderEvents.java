package dev.xkmc.l2backpack.content.render;

import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class RenderEvents {


	private static final String REG_NAME = "backpack";
	public static final ModelLayerLocation BACKPACK_LAYER = new ModelLayerLocation(new ResourceLocation(L2Backpack.MODID, REG_NAME), "main");

	public static void register(IEventBus bus) {
		bus.addListener(RenderEvents::registerReloadListener);
		bus.addListener(RenderEvents::registerLayer);
	}

	private static void onModelRegistry(ModelEvent.RegisterGeometryLoaders event) {
		//event.register(REG_NAME, BackpackDynamicModel.Loader.INSTANCE);
	}

	public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener((ResourceManagerReloadListener) resourceManager -> registerBackpackLayer());
	}

	private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		//event.registerEntityRenderer(EVERLASTING_BACKPACK_ITEM_ENTITY.get(), ItemEntityRenderer::new);
		//event.registerBlockEntityRenderer(ModBlocks.BACKPACK_TILE_TYPE.get(), context -> new BackpackBlockEntityRenderer());
	}

	public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(BACKPACK_LAYER, BackpackModel::createBodyLayer);
	}

	private static void registerBackpackLayer() {
		EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
		Map<String, EntityRenderer<? extends Player>> skinMap = renderManager.getSkinMap();
		for (EntityRenderer<? extends Player> renderer : skinMap.values()) {
			if (renderer instanceof LivingEntityRenderer livingEntityRenderer) {
				livingEntityRenderer.addLayer(new BackpackLayerRenderer(livingEntityRenderer, Minecraft.getInstance().getEntityModels()));
			}
		}
		renderManager.renderers.forEach((e, r) -> {
			if (r instanceof LivingEntityRenderer livingEntityRenderer) {
				livingEntityRenderer.addLayer(new BackpackLayerRenderer(livingEntityRenderer, Minecraft.getInstance().getEntityModels()));
			}
		});
	}

}
