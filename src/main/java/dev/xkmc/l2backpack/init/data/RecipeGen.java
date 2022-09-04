package dev.xkmc.l2backpack.init.data;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import dev.xkmc.l2backpack.init.registrate.BackpackRecipe;
import dev.xkmc.l2library.base.recipe.CustomShapelessBuilder;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.repack.registrate.util.DataIngredient;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
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
						.group("backpack_craft").pattern(" A ").pattern("DCD").pattern("BBB")
						.define('A', Tags.Items.LEATHER).define('B', wool)
						.define('C', Items.CHEST).define('D', Items.IRON_INGOT)
						.save(pvd, L2Backpack.MODID + ":shaped/craft_backpack_" + color.getName());

				unlock(pvd, new CustomShapelessBuilder<>(BackpackRecipe.RSC_BAG_DYE, backpack, 1)::unlockedBy, backpack)
						.group("backpack_dye").requires(Ingredient.of(ItemTags.BACKPACKS.tag))
						.requires(Ingredient.of(dye)).save(pvd, L2Backpack.MODID + ":shapeless/dye_backpack_" + color.getName());

				unlock(pvd, new UpgradeRecipeBuilder(BackpackRecipe.RSC_BAG_UPGRADE.get(), Ingredient.of(backpack),
						Ingredient.of(BackpackItems.ENDER_POCKET.get()), backpack)::unlocks, backpack)
						.save(pvd, L2Backpack.MODID + ":smithing/upgrade_backpack_" + color.getName());

				Item storage = BackpackItems.DIMENSIONAL_STORAGE[i].get();

				unlock(pvd, new ShapedRecipeBuilder(storage, 1)::unlockedBy, storage)
						.group("dimensional_storage_craft").pattern("EAE").pattern("DCD").pattern("BAB")
						.define('A', BackpackItems.ENDER_POCKET.get()).define('B', wool)
						.define('C', Items.ENDER_CHEST).define('D', Items.POPPED_CHORUS_FRUIT)
						.define('E', Items.GOLD_NUGGET)
						.save(pvd, L2Backpack.MODID + ":shaped/craft_storage_" + color.getName());
			}
			Item ender = BackpackItems.ENDER_BACKPACK.get();
			unlock(pvd, new ShapedRecipeBuilder(ender, 1)::unlockedBy, ender)
					.pattern("EAE").pattern("BCB").pattern("DDD")
					.define('A', Tags.Items.LEATHER).define('B', Items.IRON_INGOT)
					.define('C', Items.ENDER_CHEST).define('D', Items.PURPLE_WOOL)
					.define('E', Items.GOLD_NUGGET)
					.save(pvd);
			ender = BackpackItems.ENDER_POCKET.get();
			unlock(pvd, new ShapedRecipeBuilder(ender, 4)::unlockedBy, ender)
					.pattern("ADA").pattern("BCB").pattern("ADA")
					.define('C', Items.ENDER_PEARL).define('B', Items.GOLD_NUGGET)
					.define('A', Tags.Items.LEATHER).define('D', Items.LAPIS_LAZULI)
					.save(pvd);
		}
		{
			Item ender = BackpackItems.ENDER_POCKET.get();
			Item bag = BackpackItems.ARMOR_BAG.get();
			unlock(pvd, new ShapedRecipeBuilder(bag, 1)::unlockedBy, ender)
					.pattern("DCD").pattern("ABA").pattern(" A ")
					.define('A', Tags.Items.LEATHER).define('B', ender)
					.define('D', Items.STRING).define('C', Items.IRON_CHESTPLATE)
					.save(pvd);
			bag = BackpackItems.BOOK_BAG.get();
			unlock(pvd, new ShapedRecipeBuilder(bag, 1)::unlockedBy, ender)
					.pattern("DCD").pattern("ABA").pattern(" A ")
					.define('A', Tags.Items.LEATHER).define('B', ender)
					.define('D', Items.STRING).define('C', Items.BOOK)
					.save(pvd);

			bag = BackpackItems.ARROW_BAG.get();
			unlock(pvd, new ShapedRecipeBuilder(bag, 1)::unlockedBy, Items.ARROW)
					.pattern(" AB").pattern("ABA").pattern(" AD")
					.define('A', Tags.Items.LEATHER).define('B', Items.ARROW)
					.define('D', Items.STRING)
					.save(pvd);

			bag = BackpackItems.DRAWER.get();
			unlock(pvd, new ShapedRecipeBuilder(bag, 1)::unlockedBy, ender)
					.pattern("CAC").pattern("ABA").pattern("DAD")
					.define('A', Items.GLASS).define('B', ender)
					.define('C', Tags.Items.DYES_PURPLE)
					.define('D', Tags.Items.DYES_YELLOW)
					.save(pvd);


			bag = BackpackItems.ENDER_DRAWER.get();
			unlock(pvd, new ShapedRecipeBuilder(bag, 1)::unlockedBy, ender)
					.pattern("DAD").pattern("ABA").pattern("DED")
					.define('B', ender).define('A', Items.GLASS)
					.define('D', Items.OBSIDIAN).define('E', Items.ENDER_CHEST)
					.save(pvd);

			unlock(pvd, new ShapelessRecipeBuilder(bag, 1)::unlockedBy, bag)
					.requires(bag).save(pvd, new ResourceLocation(L2Backpack.MODID, "shapeless/clear_ender_drawer"));
		}
	}

	private static <T> T unlock(RegistrateRecipeProvider pvd, BiFunction<String, InventoryChangeTrigger.TriggerInstance, T> func, Item item) {
		return func.apply("has_" + pvd.safeName(item), DataIngredient.items(item).getCritereon(pvd));
	}

}
