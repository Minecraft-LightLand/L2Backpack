package dev.xkmc.l2backpack.init.registrate;

import com.mojang.serialization.Codec;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2backpack.content.common.BaseBagContainer;
import dev.xkmc.l2backpack.content.recipe.BackpackDyeRecipe;
import dev.xkmc.l2backpack.content.recipe.BackpackUpgradeRecipe;
import dev.xkmc.l2backpack.content.recipe.BackpackUpgradeRecipeOld;
import dev.xkmc.l2backpack.content.recipe.MultiSwitchCraftRecipe;
import dev.xkmc.l2backpack.content.restore.*;
import dev.xkmc.l2backpack.init.loot.BackpackLootModifier;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.events.screen.base.ScreenTrackerRegistry;
import dev.xkmc.l2library.init.events.screen.source.MenuSourceRegistry;
import dev.xkmc.l2library.init.events.screen.source.PlayerSlot;
import dev.xkmc.l2library.init.events.screen.source.SimpleSlotData;
import dev.xkmc.l2library.init.events.screen.track.ItemBasedTraceData;
import dev.xkmc.l2library.init.events.screen.track.MenuTraceRegistry;
import dev.xkmc.l2library.init.events.screen.track.TrackedEntry;
import dev.xkmc.l2library.serial.recipe.AbstractOldSmithingRecipe;
import dev.xkmc.l2library.serial.recipe.AbstractShapedRecipe;
import dev.xkmc.l2library.serial.recipe.AbstractShapelessRecipe;
import dev.xkmc.l2library.serial.recipe.AbstractSmithingRecipe;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

import static dev.xkmc.l2backpack.init.L2Backpack.REGISTRATE;

public class BackpackMisc {

	public static final RegistryEntry<AbstractShapelessRecipe.Serializer<BackpackDyeRecipe>> RSC_BAG_DYE =
			REGISTRATE.simple("backpack_dye", ForgeRegistries.Keys.RECIPE_SERIALIZERS, () -> new AbstractShapelessRecipe.Serializer<>(BackpackDyeRecipe::new));
	public static final RegistryEntry<AbstractSmithingRecipe.Serializer<BackpackUpgradeRecipe>> RSC_BAG_UPGRADE =
			REGISTRATE.simple("backpack_upgrade", ForgeRegistries.Keys.RECIPE_SERIALIZERS, () -> new AbstractSmithingRecipe.Serializer<>(BackpackUpgradeRecipe::new));
	public static final RegistryEntry<AbstractShapedRecipe.Serializer<MultiSwitchCraftRecipe>> RSC_BAG_CRAFT =
			REGISTRATE.simple("multiswitch_craft", ForgeRegistries.Keys.RECIPE_SERIALIZERS, () -> new AbstractShapedRecipe.Serializer<>(MultiSwitchCraftRecipe::new));

	public static final RegistryEntry<AbstractOldSmithingRecipe.Serializer<BackpackUpgradeRecipeOld>> RSC_BAG_UPGRADE_OLD =
			REGISTRATE.simple("backpack_upgrade_old", ForgeRegistries.Keys.RECIPE_SERIALIZERS, () -> new AbstractOldSmithingRecipe.Serializer<>(BackpackUpgradeRecipeOld::new));

	public static final RegistryEntry<Codec<BackpackLootModifier>> SER = REGISTRATE.simple("main", ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, () -> BackpackLootModifier.CODEC);

	public static final RegistryEntry<DimensionItemSource> IS_DIM = L2Library.REGISTRATE.simple("dimension", ScreenTrackerRegistry.ITEM_SOURCE.key(), DimensionItemSource::new);
	public static final RegistryEntry<DimensionTrace> TE_DIM = L2Library.REGISTRATE.simple("dimension", ScreenTrackerRegistry.TRACKED_ENTRY_TYPE.key(), DimensionTrace::new);
	public static final RegistryEntry<BackpackTrace> TE_BAG = L2Library.REGISTRATE.simple("backpack", ScreenTrackerRegistry.TRACKED_ENTRY_TYPE.key(), BackpackTrace::new);

	public static void register(IEventBus bus) {
	}

	public static void commonSetup() {

		MenuSourceRegistry.register(BackpackMenu.MT_ES.get(), (menu, slot, index, wid) ->
				index >= 36 && index < 63 ?
						Optional.of(new PlayerSlot<>(ScreenTrackerRegistry.IS_ENDER.get(), new SimpleSlotData(index - 36))) :
						Optional.empty());

		MenuSourceRegistry.register(BackpackMenu.MT_WORLD_CHEST.get(), (menu, slot, index, wid) ->
				Optional.of(new PlayerSlot<>(IS_DIM.get(),
						new DimensionSourceData(menu.getColor(), index - 36, menu.getOwner()))));

		MenuTraceRegistry.register(BackpackMenu.MT_WORLD_CHEST.get(), menu ->
				Optional.of(TrackedEntry.of(TE_DIM.get(),
						new DimensionTraceData(menu.getColor(), menu.getOwner()))));

		addBag(BackpackMenu.MT_BACKPACK);
		addBag(BackpackMenu.MT_ARMOR);
		addBag(BackpackMenu.MT_ARROW);
		addBag(BackpackMenu.MT_TOOL);
		addBag(BackpackMenu.MT_MULTI);
		addBag(BackpackMenu.MT_ES);
	}

	private static <T extends BaseBagContainer<T>> void addBag(MenuEntry<T> type) {
		MenuTraceRegistry.register(type.get(), menu ->
				Optional.of(TrackedEntry.of(TE_BAG.get(),
						new ItemBasedTraceData(menu.item_slot,
								menu.getStack().getItem()))));
	}

}
