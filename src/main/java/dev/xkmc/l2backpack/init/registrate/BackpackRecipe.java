package dev.xkmc.l2backpack.init.registrate;

import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2backpack.content.recipe.BackpackDyeRecipe;
import dev.xkmc.l2backpack.content.recipe.BackpackUpgradeRecipe;
import dev.xkmc.l2backpack.content.recipe.MultiSwitchCraftRecipe;
import dev.xkmc.l2library.base.recipe.AbstractShapedRecipe;
import dev.xkmc.l2library.base.recipe.AbstractShapelessRecipe;
import dev.xkmc.l2library.base.recipe.AbstractSmithingRecipe;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;

import static dev.xkmc.l2backpack.init.L2Backpack.REGISTRATE;

public class BackpackRecipe {

	public static final RegistryEntry<AbstractShapelessRecipe.Serializer<BackpackDyeRecipe>> RSC_BAG_DYE =
			REGISTRATE.simple("backpack_dye", ForgeRegistries.Keys.RECIPE_SERIALIZERS, () -> new AbstractShapelessRecipe.Serializer<>(BackpackDyeRecipe::new));
	public static final RegistryEntry<AbstractSmithingRecipe.Serializer<BackpackUpgradeRecipe>> RSC_BAG_UPGRADE =
			REGISTRATE.simple("backpack_upgrade", ForgeRegistries.Keys.RECIPE_SERIALIZERS, () -> new AbstractSmithingRecipe.Serializer<>(BackpackUpgradeRecipe::new));
	public static final RegistryEntry<AbstractShapedRecipe.Serializer<MultiSwitchCraftRecipe>> RSC_BAG_CRAFT =
			REGISTRATE.simple("multiswitch_craft", ForgeRegistries.Keys.RECIPE_SERIALIZERS, () -> new AbstractShapedRecipe.Serializer<>(MultiSwitchCraftRecipe::new));


	public static void register(IEventBus bus) {
	}

}
