package dev.xkmc.l2backpack.init;

import dev.xkmc.l2backpack.content.arrowbag.ArrowBag;
import dev.xkmc.l2backpack.content.arrowbag.ArrowBagOverlay;
import dev.xkmc.l2backpack.content.backpack.BackpackItem;
import dev.xkmc.l2backpack.content.backpack.EnderBackpackItem;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.render.RenderEvents;
import dev.xkmc.l2backpack.init.data.Keys;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import dev.xkmc.l2library.repack.registrate.util.entry.ItemEntry;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class L2BackpackClient {

	public static void onCtorClient(IEventBus bus, IEventBus eventBus) {
		bus.addListener(L2BackpackClient::clientSetup);
		bus.addListener(L2BackpackClient::registerOverlay);
		bus.addListener(L2BackpackClient::registerKeys);
		bus.addListener(L2BackpackClient::registerChestTextures);
		RenderEvents.register(bus);
	}

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			for (ItemEntry<BackpackItem> entry : BackpackItems.BACKPACKS) {
				ItemProperties.register(entry.get(), new ResourceLocation("open"), BaseBagItem::isOpened);
			}
			ItemProperties.register(BackpackItems.ENDER_BACKPACK.get(), new ResourceLocation("open"), EnderBackpackItem::isOpened);
			ItemProperties.register(BackpackItems.ARROW_BAG.get(), new ResourceLocation(L2Backpack.MODID, "arrow"), (stack, level, entity, i) -> ArrowBag.displayArrow(stack));

		});
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerOverlay(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "arrow_bag", new ArrowBagOverlay());
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerKeys(RegisterKeyMappingsEvent event) {
		for (Keys k : Keys.values())
			event.register(k.map);
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerChestTextures(TextureStitchEvent.Pre event) {
		if (event.getAtlas().location().equals(Sheets.CHEST_SHEET)) {
			for (DyeColor color : DyeColor.values()) {
				event.addSprite(new ResourceLocation(L2Backpack.MODID, "entity/" + color.getName()));
			}
		}
	}

}
