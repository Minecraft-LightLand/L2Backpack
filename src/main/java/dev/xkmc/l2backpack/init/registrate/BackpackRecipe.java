package dev.xkmc.l2backpack.init.registrate;

import dev.xkmc.l2library.base.recipe.AbstractShapelessRecipe;
import dev.xkmc.l2library.base.recipe.AbstractSmithingRecipe;
import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2backpack.content.recipe.BackpackDyeRecipe;
import dev.xkmc.l2backpack.content.recipe.BackpackUpgradeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;

import static dev.xkmc.l2backpack.init.L2Backpack.REGISTRATE;

public class BackpackRecipe {

	public static final RegistryEntry<AbstractShapelessRecipe.Serializer<BackpackDyeRecipe>> RSC_BAG_DYE =
			REGISTRATE.simple("backpack_dye", ForgeRegistries.Keys.RECIPE_SERIALIZERS, () -> new AbstractShapelessRecipe.Serializer<>(BackpackDyeRecipe::new));
	public static final RegistryEntry<AbstractSmithingRecipe.Serializer<BackpackUpgradeRecipe>> RSC_BAG_UPGRADE =
			REGISTRATE.simple("backpack_upgrade", ForgeRegistries.Keys.RECIPE_SERIALIZERS, () -> new AbstractSmithingRecipe.Serializer<>(BackpackUpgradeRecipe::new));


	public static void register(IEventBus bus) {
	}

}