package dev.xkmc.l2backpack.init.registrate;

import com.tterrag.registrate.util.entry.MenuEntry;
import dev.xkmc.l2backpack.content.capability.PickupModeCap;
import dev.xkmc.l2backpack.content.common.BaseBagMenu;
import dev.xkmc.l2backpack.content.recipe.BackpackDyeRecipe;
import dev.xkmc.l2backpack.content.recipe.BackpackUpgradeRecipe;
import dev.xkmc.l2backpack.content.recipe.DrawerUpgradeRecipe;
import dev.xkmc.l2backpack.content.remote.player.EnderSyncCap;
import dev.xkmc.l2backpack.content.restore.*;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.loot.BackpackLootModifier;
import dev.xkmc.l2core.capability.player.PlayerCapabilityNetworkHandler;
import dev.xkmc.l2core.init.reg.simple.*;
import dev.xkmc.l2core.serial.recipe.AbstractShapelessRecipe;
import dev.xkmc.l2core.serial.recipe.AbstractSmithingRecipe;
import dev.xkmc.l2menustacker.screen.base.L2MSReg;
import dev.xkmc.l2menustacker.screen.source.ItemSource;
import dev.xkmc.l2menustacker.screen.source.MenuSourceRegistry;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import dev.xkmc.l2menustacker.screen.track.ItemBasedTraceData;
import dev.xkmc.l2menustacker.screen.track.MenuTraceRegistry;
import dev.xkmc.l2menustacker.screen.track.TrackedEntry;
import dev.xkmc.l2menustacker.screen.track.TrackedEntryType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;

public class LBMisc {

	private static final SR<RecipeSerializer<?>> RS = SR.of(L2Backpack.REG, BuiltInRegistries.RECIPE_SERIALIZER);
	public static final Val<AbstractShapelessRecipe.Serializer<BackpackDyeRecipe>> RSC_BAG_DYE =
			RS.reg("backpack_dye", () -> new AbstractShapelessRecipe.Serializer<>(BackpackDyeRecipe::new));
	public static final Val<AbstractSmithingRecipe.Serializer<BackpackUpgradeRecipe>> RSC_BAG_UPGRADE =
			RS.reg("backpack_upgrade", () -> new AbstractSmithingRecipe.Serializer<>(BackpackUpgradeRecipe::new));
	public static final Val<AbstractSmithingRecipe.Serializer<DrawerUpgradeRecipe>> RSC_DRAWER_UPGRADE =
			RS.reg("drawer_upgrade", () -> new AbstractSmithingRecipe.Serializer<>(DrawerUpgradeRecipe::new));

	private static final CdcReg<IGlobalLootModifier> GLM = CdcReg.of(L2Backpack.REG, NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS);
	public static final CdcVal<BackpackLootModifier> SER = GLM.reg("main", BackpackLootModifier.MAP_CODEC);

	private static final SR<ItemSource<?>> SOURCE = SR.of(L2Backpack.REG, L2MSReg.ITEM_SOURCE.key());
	public static final Val<DimensionItemSource> IS_DIM = SOURCE.reg("dimension", DimensionItemSource::new);
	private static final SR<TrackedEntryType<?>> TRACKED = SR.of(L2Backpack.REG, L2MSReg.TRACKED_ENTRY_TYPE.key());
	public static final Val<DimensionTrace> TE_DIM = TRACKED.reg("dimension", DimensionTrace::new);
	public static final Val<BackpackTrace> TE_BAG = TRACKED.reg("backpack", BackpackTrace::new);

	public static final AttReg ATT = AttReg.of(L2Backpack.REG);
	public static final AttVal.PlayerVal<EnderSyncCap> ENDER_SYNC = ATT.player("player_ender",
			EnderSyncCap.class, EnderSyncCap::new, PlayerCapabilityNetworkHandler::new);

	public static final ItemCapability<PickupModeCap, Void> PICKUP = ItemCapability.createVoid(L2Backpack.loc("pickup"), PickupModeCap.class);

	public static void register() {
	}

	public static void commonSetup() {
		MenuSourceRegistry.register(LBMenu.MT_WORLD_CHEST.get(), (menu, slot, index, wid) ->
				Optional.of(new PlayerSlot<>(IS_DIM.get(),
						new DimensionSourceData(menu.getColor(), index - 36, menu.getOwner()))));

		MenuTraceRegistry.register(LBMenu.MT_WORLD_CHEST.get(), menu ->
				Optional.of(TrackedEntry.of(TE_DIM.get(),
						new DimensionTraceData(menu.getColor(), menu.getOwner()))));

		addBag(LBMenu.MT_BACKPACK);
		addBag(LBMenu.MT_ARMOR_SET);
		addBag(LBMenu.MT_9);
	}

	private static <T extends BaseBagMenu<T>> void addBag(MenuEntry<T> type) {
		MenuTraceRegistry.register(type.get(), menu ->
				Optional.of(TrackedEntry.of(TE_BAG.get(),
						new ItemBasedTraceData(menu.item_slot,
								menu.getStack().getItem()))));
	}

}
