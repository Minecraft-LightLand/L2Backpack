package dev.xkmc.l2backpack.init;

import dev.xkmc.l2backpack.content.bag.AbstractBag;
import dev.xkmc.l2backpack.content.client.*;
import dev.xkmc.l2backpack.content.client.QuickSwapSlotOverlay;
import dev.xkmc.l2backpack.content.common.InvClientTooltip;
import dev.xkmc.l2backpack.content.common.InvTooltip;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapOverlay;
import dev.xkmc.l2backpack.content.quickswap.single.Quiver;
import dev.xkmc.l2backpack.init.data.LBKeys;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(value = Dist.CLIENT, modid = L2Backpack.MODID, bus = EventBusSubscriber.Bus.MOD)
public class L2BackpackClient {

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ItemProperties.register(LBItems.QUIVER.get(), L2Backpack.loc("arrow"), (stack, level, entity, i) -> Quiver.displayArrow(stack));

			ClampedItemPropertyFunction func = (stack, level, entity, i) -> AbstractBag.isFilled(stack) ? 1 : 0;
			for (var e : AbstractBag.getAllBags())
				ItemProperties.register(e, L2Backpack.loc("fill"), func);
		});
	}

	@SubscribeEvent
	public static void registerClientTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(InvTooltip.class, InvClientTooltip::new);
	}

	@SubscribeEvent
	public static void registerOverlay(RegisterGuiLayersEvent event) {
		event.registerAbove(VanillaGuiLayers.CROSSHAIR, L2Backpack.loc("arrow_bag"), new QuickSwapOverlay());
		event.registerAbove(VanillaGuiLayers.CROSSHAIR, L2Backpack.loc("ender_drawer"), new EnderPreviewOverlay());
		event.registerAbove(VanillaGuiLayers.HOTBAR, L2Backpack.loc("quick_swap_slot"), QuickSwapSlotOverlay.INSTANCE);
	}

	@SubscribeEvent
	public static void registerDeco(RegisterItemDecorationsEvent event) {
		{
			var deco = new DrawerCountDeco();
			event.register(LBItems.DRAWER.get(), deco);
			event.register(LBItems.ENDER_DRAWER.get(), deco);
		}
		{
			var deco = new BagCountDeco();
			for (var e : AbstractBag.getAllBags())
				event.register(e, deco);
		}
	}

	@SubscribeEvent
	public static void registerReloadListener(EntityRenderersEvent.AddLayers event) {
		RenderEvents.registerBackpackLayer(event);
	}

	@SubscribeEvent
	public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(RenderEvents.BACKPACK_LAYER, BackpackModel::createBodyLayer);
	}

	@SubscribeEvent
	public static void registerKeys(RegisterKeyMappingsEvent event) {
		for (var e : LBKeys.values()) {
			event.register(e.map);
		}
	}

}
