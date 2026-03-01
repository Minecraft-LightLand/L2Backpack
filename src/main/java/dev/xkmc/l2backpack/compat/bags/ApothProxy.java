package dev.xkmc.l2backpack.compat.bags;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemItem;
import dev.xkmc.l2backpack.init.data.RecipeGen;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

public class ApothProxy {

	public static boolean pred(ItemStack stack) {
		return !stack.isStackable() && stack.getItem() instanceof GemItem;
	}

	public static void recipe(DataGenContext<Item, CompatBag> ctx, RegistrateRecipeProvider pvd) {
		RecipeGen.unlock(pvd, new ShapedRecipeBuilder(RecipeCategory.MISC, ctx.get(), 1)::unlockedBy, BackpackItems.ENDER_POCKET.get())
				.pattern("DCD").pattern("ABA").pattern(" A ")
				.define('A', Tags.Items.LEATHER).define('B', BackpackItems.ENDER_POCKET)
				.define('D', Items.STRING).define('C', Adventure.Items.GEM_DUST.get())
				.save(pvd);
	}

}
