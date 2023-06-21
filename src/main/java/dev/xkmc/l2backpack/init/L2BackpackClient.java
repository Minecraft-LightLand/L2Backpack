package dev.xkmc.l2backpack.init;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2backpack.content.backpack.BackpackItem;
import dev.xkmc.l2backpack.content.backpack.EnderBackpackItem;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapOverlay;
import dev.xkmc.l2backpack.content.quickswap.quiver.Quiver;
import dev.xkmc.l2backpack.content.render.BackpackModel;
import dev.xkmc.l2backpack.content.render.EnderPreviewOverlay;
import dev.xkmc.l2backpack.content.render.RenderEvents;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = L2Backpack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class L2BackpackClient {

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			for (ItemEntry<BackpackItem> entry : BackpackItems.BACKPACKS) {
				ItemProperties.register(entry.get(), new ResourceLocation("open"), BaseBagItem::isOpened);
			}
			ItemProperties.register(BackpackItems.ENDER_BACKPACK.get(), new ResourceLocation("open"), EnderBackpackItem::isOpened);
			ItemProperties.register(BackpackItems.QUIVER.get(), new ResourceLocation(L2Backpack.MODID, "arrow"), (stack, level, entity, i) -> Quiver.displayArrow(stack));

		});
	}

	@SubscribeEvent
	public static void registerOverlay(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "arrow_bag", new QuickSwapOverlay());
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "ender_drawer", new EnderPreviewOverlay());
	}

	@SubscribeEvent
	public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener((ResourceManagerReloadListener) resourceManager -> RenderEvents.registerBackpackLayer());
	}

	@SubscribeEvent
	public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(RenderEvents.BACKPACK_LAYER, BackpackModel::createBodyLayer);
	}

}
