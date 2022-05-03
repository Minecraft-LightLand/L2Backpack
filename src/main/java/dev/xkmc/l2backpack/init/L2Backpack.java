package dev.xkmc.l2backpack.init;

import dev.xkmc.l2backpack.content.capability.WorldStorage;
import dev.xkmc.l2backpack.events.CapabilityEvents;
import dev.xkmc.l2backpack.events.MiscEventHandler;
import dev.xkmc.l2backpack.init.data.LangData;
import dev.xkmc.l2backpack.init.data.RecipeGen;
import dev.xkmc.l2backpack.init.registrate.LightlandBlocks;
import dev.xkmc.l2backpack.init.registrate.LightlandItems;
import dev.xkmc.l2backpack.init.registrate.LightlandMenu;
import dev.xkmc.l2backpack.init.registrate.LightlandRecipe;
import dev.xkmc.l2backpack.network.PacketHandler;
import dev.xkmc.l2library.base.LcyRegistrate;
import dev.xkmc.l2library.repack.registrate.providers.ProviderType;
import dev.xkmc.l2library.serial.handler.Handlers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("l2backpack")
public class L2Backpack {

	public static final String MODID = "l2backpack";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final LcyRegistrate REGISTRATE = new LcyRegistrate(MODID);

	private static void registerRegistrates(IEventBus bus) {
		ForgeMod.enableMilkFluid();
		LightlandBlocks.register();
		LightlandItems.register();
		LightlandMenu.register();
		LightlandRecipe.register(bus);
		Handlers.register();
		REGISTRATE.addDataGenerator(ProviderType.RECIPE, RecipeGen::genRecipe);
	}

	private static void registerForgeEvents() {
		MinecraftForge.EVENT_BUS.register(CapabilityEvents.class);
		MinecraftForge.EVENT_BUS.register(MiscEventHandler.class);

	}

	private static void registerModBusEvents(IEventBus bus) {
		bus.addListener(L2Backpack::setup);
		bus.addListener(LightLandClient::clientSetup);
		bus.addListener(EventPriority.LOWEST, L2Backpack::gatherData);
		bus.addListener(L2Backpack::registerCaps);
	}

	public L2Backpack() {
		FMLJavaModLoadingContext ctx = FMLJavaModLoadingContext.get();
		IEventBus bus = ctx.getModEventBus();
		registerModBusEvents(bus);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> LightLandClient.onCtorClient(bus, MinecraftForge.EVENT_BUS));
		registerRegistrates(bus);
		registerForgeEvents();
	}

	private static void setup(final FMLCommonSetupEvent event) {
		event.enqueueWork(PacketHandler::registerPackets);
	}

	public static void gatherData(GatherDataEvent event) {
		LangData.addTranslations(REGISTRATE::addRawLang);
	}

	public static void registerCaps(RegisterCapabilitiesEvent event) {
		event.register(WorldStorage.class);
	}

}
