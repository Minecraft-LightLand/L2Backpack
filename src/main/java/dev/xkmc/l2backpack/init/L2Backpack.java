package dev.xkmc.l2backpack.init;

import com.tterrag.registrate.providers.ProviderType;
import dev.xkmc.l2backpack.compat.GolemCompat;
import dev.xkmc.l2backpack.compat.LCCompat;
import dev.xkmc.l2backpack.compat.PatchouliClickListener;
import dev.xkmc.l2backpack.compat.PatchouliCompat;
import dev.xkmc.l2backpack.content.bag.BagCaps;
import dev.xkmc.l2backpack.content.bag.BagItemHandler;
import dev.xkmc.l2backpack.content.capability.PickupModeCap;
import dev.xkmc.l2backpack.content.common.BaseBagInvWrapper;
import dev.xkmc.l2backpack.content.common.BaseBagItemHandler;
import dev.xkmc.l2backpack.content.remote.dimensional.DimensionalCaps;
import dev.xkmc.l2backpack.content.remote.player.EnderBackpackCaps;
import dev.xkmc.l2backpack.content.remote.player.EnderSyncCap;
import dev.xkmc.l2backpack.content.remote.player.EnderSyncPacket;
import dev.xkmc.l2backpack.events.BackpackSel;
import dev.xkmc.l2backpack.events.BackpackSlotClickListener;
import dev.xkmc.l2backpack.init.data.*;
import dev.xkmc.l2backpack.init.loot.LootGen;
import dev.xkmc.l2backpack.init.registrate.*;
import dev.xkmc.l2backpack.network.*;
import dev.xkmc.l2complements.init.L2Complements;
import dev.xkmc.l2core.compat.patchouli.PatchouliHelper;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.init.reg.simple.Reg;
import dev.xkmc.l2core.util.MathHelper;
import dev.xkmc.l2itemselector.select.SelectionRegistry;
import dev.xkmc.l2serial.network.PacketHandler;
import dev.xkmc.l2serial.serialization.custom_handler.Handlers;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.patchouli.api.PatchouliAPI;

import static dev.xkmc.l2serial.network.PacketHandler.NetDir.PLAY_TO_CLIENT;
import static dev.xkmc.l2serial.network.PacketHandler.NetDir.PLAY_TO_SERVER;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(L2Backpack.MODID)
@EventBusSubscriber(modid = L2Backpack.MODID, bus = EventBusSubscriber.Bus.MOD)
public class L2Backpack {

	public static final String MODID = "l2backpack";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Reg REG = new Reg(MODID);
	public static final L2Registrate REGISTRATE = new L2Registrate(MODID);
	public static final BackpackSlotClickListener SLOT_CLICK = new BackpackSlotClickListener();

	public static final PacketHandler HANDLER = new PacketHandler(MODID, 3,
			e -> e.create(ClickInteractToServer.class, PLAY_TO_SERVER),
			e -> e.create(CreativeSetCarryToClient.class, PLAY_TO_CLIENT),
			e -> e.create(CreativeSetCarryToServer.class, PLAY_TO_SERVER),
			e -> e.create(RequestTooltipUpdateEvent.class, PLAY_TO_SERVER),
			e -> e.create(RespondTooltipUpdateEvent.class, PLAY_TO_CLIENT),
			e -> e.create(EnderSyncPacket.class, PLAY_TO_CLIENT)
	);

	public static final PatchouliHelper PATCHOULI = new PatchouliHelper(REGISTRATE, "backpack_guide");

	public L2Backpack() {
		LBBlocks.register();
		LBItems.register();
		LBMenu.register();
		LBMisc.register();
		LBTriggers.register();
		LBConfig.init();
		PickupModeCap.register();
		EnderSyncCap.register();
		if (ModList.get().isLoaded(ModularGolems.MODID)) GolemCompat.register();
		if (ModList.get().isLoaded(L2Complements.MODID)) NeoForge.EVENT_BUS.register(LCCompat.class);
		REGISTRATE.addDataGenerator(ProviderType.RECIPE, LBRecipeGen::genRecipe);
		REGISTRATE.addDataGenerator(ProviderType.ADVANCEMENT, LBAdvGen::genAdvancements);
		REGISTRATE.addDataGenerator(ProviderType.LOOT, LootGen::genLoot);
		REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, LBTagGen::onBlockTagGen);
		REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, LBTagGen::onItemTagGen);
		if (ModList.get().isLoaded(PatchouliAPI.MOD_ID)) {
			PatchouliCompat.gen();
			new PatchouliClickListener();
			NeoForge.EVENT_BUS.register(PatchouliClickListener.class);
		}
		SelectionRegistry.register(1000, BackpackSel.INSTANCE);
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		// items
		{
			var backpacks = MathHelper.merge(LBItems.BACKPACKS,
					LBItems.QUIVER, LBItems.SCABBARD, LBItems.ARMOR_SWAP, LBItems.SUIT_SWAP);

			event.registerItem(LBMisc.PICKUP, (stack, c) -> new BaseBagInvWrapper(stack), backpacks);
			event.registerItem(LBMisc.PICKUP, (stack, c) -> new EnderBackpackCaps(stack), LBItems.ENDER_BACKPACK);
			event.registerItem(LBMisc.PICKUP, (stack, c) -> new DimensionalCaps(stack), LBItems.DIMENSIONAL_STORAGE);
			event.registerItem(LBMisc.PICKUP, LBItems.DRAWER.get()::getCaps, LBItems.DRAWER);
			event.registerItem(LBMisc.PICKUP, LBItems.ENDER_DRAWER.get()::getCaps, LBItems.ENDER_DRAWER);
			event.registerItem(LBMisc.PICKUP, (stack, c) -> new BagCaps(stack), LBItems.ARMOR_BAG, LBItems.BOOK_BAG);

			event.registerItem(Capabilities.ItemHandler.ITEM, (stack, c) -> new BaseBagItemHandler(stack), backpacks);
			event.registerItem(Capabilities.ItemHandler.ITEM, (stack, c) -> new BagItemHandler(stack), LBItems.ARMOR_BAG, LBItems.BOOK_BAG);
		}
		// blocks
		{
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, LBBlocks.TE_DRAWER.get(), (be, dir) -> be.handler);
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, LBBlocks.TE_ENDER_DRAWER.get(), (be, dir) -> be.getItemHandler());
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, LBBlocks.TE_DIMENSIONAL.get(), (be, dir) -> be.getItemHandler());
		}
	}

	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(LBMisc::commonSetup);
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		LBLang.addTranslations(REGISTRATE::addRawLang);
		var gen = event.getGenerator();
		boolean server = event.includeServer();
		gen.addProvider(server, new SlotGen(gen.getPackOutput(), event.getExistingFileHelper(), event.getLookupProvider()));
		//TODO event.getGenerator().addProvider(event.includeServer(), new BackpackGLMProvider(event.getGenerator().getPackOutput()));
	}

	public static ResourceLocation loc(String id) {
		return ResourceLocation.fromNamespaceAndPath(MODID, id);
	}

}
