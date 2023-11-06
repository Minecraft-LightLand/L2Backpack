package dev.xkmc.l2backpack.init;

import com.tterrag.registrate.providers.ProviderType;
import dev.xkmc.l2backpack.compat.GolemCompat;
import dev.xkmc.l2backpack.content.capability.BackpackCap;
import dev.xkmc.l2backpack.content.remote.common.WorldStorage;
import dev.xkmc.l2backpack.content.remote.player.EnderSyncCap;
import dev.xkmc.l2backpack.content.remote.player.EnderSyncPacket;
import dev.xkmc.l2backpack.events.BackpackSel;
import dev.xkmc.l2backpack.events.BackpackSlotClickListener;
import dev.xkmc.l2backpack.init.advancement.BackpackTriggers;
import dev.xkmc.l2backpack.init.data.*;
import dev.xkmc.l2backpack.init.loot.LootGen;
import dev.xkmc.l2backpack.init.registrate.BackpackBlocks;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import dev.xkmc.l2backpack.init.registrate.BackpackMenus;
import dev.xkmc.l2backpack.init.registrate.BackpackMisc;
import dev.xkmc.l2backpack.network.*;
import dev.xkmc.l2itemselector.select.SelectionRegistry;
import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.serial.config.PacketHandler;
import dev.xkmc.l2serial.serialization.custom_handler.Handlers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;
import static net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(L2Backpack.MODID)
@Mod.EventBusSubscriber(modid = L2Backpack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class L2Backpack {

	public static final String MODID = "l2backpack";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final L2Registrate REGISTRATE = new L2Registrate(MODID);
	public static final BackpackSlotClickListener SLOT_CLICK = new BackpackSlotClickListener();

	public static final PacketHandler HANDLER = new PacketHandler(
			new ResourceLocation(MODID, "main"), 2,
			e -> e.create(DrawerInteractToServer.class, PLAY_TO_SERVER),
			e -> e.create(CreativeSetCarryToClient.class, PLAY_TO_CLIENT),
			e -> e.create(CreativeSetCarryToServer.class, PLAY_TO_SERVER),
			e -> e.create(RequestTooltipUpdateEvent.class, PLAY_TO_SERVER),
			e -> e.create(RespondTooltipUpdateEvent.class, PLAY_TO_CLIENT),
			e -> e.create(EnderSyncPacket.class, PLAY_TO_CLIENT)
	);

	private static void registerRegistrates(IEventBus bus) {
		ForgeMod.enableMilkFluid();
		BackpackBlocks.register();
		BackpackItems.register();
		BackpackMenus.register();
		BackpackMisc.register(bus);
		Handlers.register();
		BackpackTriggers.register();
		BackpackConfig.init();
		BackpackCap.register();
		EnderSyncCap.register();
		if (ModList.get().isLoaded("modulargolems")) GolemCompat.register();
		REGISTRATE.addDataGenerator(ProviderType.RECIPE, RecipeGen::genRecipe);
		REGISTRATE.addDataGenerator(ProviderType.ADVANCEMENT, AdvGen::genAdvancements);
		REGISTRATE.addDataGenerator(ProviderType.LOOT, LootGen::genLoot);
		REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, TagGen::onBlockTagGen);
		REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, TagGen::onItemTagGen);
	}

	public L2Backpack() {
		FMLJavaModLoadingContext ctx = FMLJavaModLoadingContext.get();
		IEventBus bus = ctx.getModEventBus();
		registerRegistrates(bus);
		SelectionRegistry.register(-1000, BackpackSel.INSTANCE);
	}

	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(BackpackMisc::commonSetup);
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		LangData.addTranslations(REGISTRATE::addRawLang);
		var gen = event.getGenerator();
		boolean server = event.includeServer();
		gen.addProvider(server, new SlotGen(gen));
		//TODO event.getGenerator().addProvider(event.includeServer(), new BackpackGLMProvider(event.getGenerator().getPackOutput()));
	}

	@SubscribeEvent
	public static void registerCaps(RegisterCapabilitiesEvent event) {
		event.register(WorldStorage.class);
	}

}
