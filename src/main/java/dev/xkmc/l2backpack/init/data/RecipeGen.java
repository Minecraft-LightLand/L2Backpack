package dev.xkmc.l2backpack.init.data;

import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import dev.xkmc.l2backpack.init.registrate.BackpackRecipe;
import dev.xkmc.l2library.base.recipe.CustomShapelessBuilder;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.repack.registrate.util.DataIngredient;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.BiFunction;

public class RecipeGen {

	public static void genRecipe(RegistrateRecipeProvider pvd) {
		{
			for (int i = 0; i < 16; i++) {
				DyeColor color = DyeColor.values()[i];
				Item wool = ForgeRegistries.ITEMS.getValue(new ResourceLocation(color.getName() + "_wool"));
				Item dye = ForgeRegistries.ITEMS.getValue(new ResourceLocation(color.getName() + "_dye"));
				Item backpack = BackpackItems.BACKPACKS[i].get();

				unlock(pvd, new ShapedRecipeBuilder(backpack, 1)::unlockedBy, backpack)
						.group("backpack_craft").pattern("ADA").pattern("BCB").pattern("ADA")
						.define('A', Tags.Items.LEATHER).define('B', wool)
						.define('C', Items.CHEST).define('D', Items.IRON_INGOT)
						.save(pvd, "lightland:shaped/craft_backpack_" + color.getName());

				unlock(pvd, new CustomShapelessBuilder<>(BackpackRecipe.RSC_BAG_DYE, backpack, 1)::unlockedBy, backpack)
						.group("backpack_dye").requires(Ingredient.of(ItemTags.BACKPACKS.tag))
						.requires(Ingredient.of(dye)).save(pvd, "lightland:shapeless/dye_backpack_" + color.getName());

				unlock(pvd, new UpgradeRecipeBuilder(BackpackRecipe.RSC_BAG_UPGRADE.get(), Ingredient.of(backpack),
						Ingredient.of(BackpackItems.ENDER_POCKET.get()), backpack)::unlocks, backpack)
						.save(pvd, "lightland:smithing/upgrade_backpack_" + color.getName());

				Item storage = BackpackItems.DIMENSIONAL_STORAGE[i].get();

				unlock(pvd, new ShapedRecipeBuilder(storage, 1)::unlockedBy, storage)
						.group("dimensional_storage_craft").pattern("DAD").pattern("ACA").pattern("BAB")
						.define('A', BackpackItems.ENDER_POCKET.get()).define('B', wool)
						.define('C', Items.ENDER_CHEST).define('D', Items.POPPED_CHORUS_FRUIT)
						.save(pvd, "lightland:shaped/craft_storage_" + color.getName());
			}
			Item ender = BackpackItems.ENDER_BACKPACK.get();
			unlock(pvd, new ShapedRecipeBuilder(ender, 1)::unlockedBy, ender)
					.pattern("ADA").pattern("BCB").pattern("ADA")
					.define('A', Tags.Items.LEATHER).define('B', Items.ENDER_PEARL)
					.define('C', Items.ENDER_CHEST).define('D', Items.IRON_INGOT)
					.save(pvd);
			ender = BackpackItems.ENDER_POCKET.get();
			unlock(pvd, new ShapedRecipeBuilder(ender, 1)::unlockedBy, ender)
					.pattern("ADA").pattern("BCB").pattern("ADA")
					.define('C', Tags.Items.LEATHER).define('B', Items.GOLD_INGOT)
					.define('A', Items.ENDER_PEARL).define('D', Items.LAPIS_LAZULI)
					.save(pvd);
		}
		{
			Item ender = BackpackItems.ENDER_POCKET.get();
			Item bag = BackpackItems.ARMOR_BAG.get();
			unlock(pvd, new ShapedRecipeBuilder(bag, 1)::unlockedBy, ender)
					.pattern("DCD").pattern("ABA").pattern("AAA")
					.define('A', Tags.Items.LEATHER).define('B', ender)
					.define('D', Items.STRING).define('C', Items.IRON_CHESTPLATE)
					.save(pvd);
			bag = BackpackItems.BOOK_BAG.get();
			unlock(pvd, new ShapedRecipeBuilder(bag, 1)::unlockedBy, ender)
					.pattern("DCD").pattern("ABA").pattern("AAA")
					.define('A', Tags.Items.LEATHER).define('B', ender)
					.define('D', Items.STRING).define('C', Items.BOOK)
					.save(pvd);

			bag = BackpackItems.ARROW_BAG.get();
			unlock(pvd, new ShapedRecipeBuilder(bag, 1)::unlockedBy, Items.ARROW)
					.pattern("DCD").pattern("ABA").pattern("AAA")
					.define('A', Tags.Items.LEATHER).define('B', Items.ARROW)
					.define('D', Items.STRING).define('C', Items.BOW)
					.save(pvd);
		}
	}

	private static <T> T unlock(RegistrateRecipeProvider pvd, BiFunction<String, InventoryChangeTrigger.TriggerInstance, T> func, Item item) {
		return func.apply("has_" + pvd.safeName(item), DataIngredient.items(item).getCritereon(pvd));
	}

}
