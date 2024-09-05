package dev.xkmc.l2backpack.content.client;

import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class RenderEvents {

	private static final String REG_NAME = "backpack";
	public static final ModelLayerLocation BACKPACK_LAYER = new ModelLayerLocation(L2Backpack.loc(REG_NAME), "main");

	@SuppressWarnings({"unchecked"})
	public static void registerBackpackLayer(EntityRenderersEvent.AddLayers event) {
		for (var e : event.getSkins()) {
			if (event.getSkin(e) instanceof LivingEntityRenderer ler) {
				addLayer(ler);
			}
		}
		for (var e : event.getEntityTypes()) {
			if (event.getRenderer(e) instanceof LivingEntityRenderer ler && ler.getModel() instanceof HumanoidModel<?>) {
				addLayer(ler);
			}
		}
	}

	private static <T extends LivingEntity, M extends HumanoidModel<T>> void addLayer(LivingEntityRenderer<T, M> ler) {
		var mc = Minecraft.getInstance();
		var ir = mc.getEntityRenderDispatcher().getItemInHandRenderer();
		ler.addLayer(new BackpackLayerRenderer<>(ler, mc.getEntityModels()));
		ler.addLayer(new ItemOnBackLayerRenderer<>(ler, mc.getEntityModels(), ir));
	}

}
