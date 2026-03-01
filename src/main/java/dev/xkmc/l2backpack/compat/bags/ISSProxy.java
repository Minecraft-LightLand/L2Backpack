package dev.xkmc.l2backpack.compat.bags;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2backpack.init.data.RecipeGen;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

public class ISSProxy {

	public static boolean pred(ItemStack stack) {
		return !stack.isStackable() && stack.getItem() instanceof Scroll;
	}

	public static void recipe(DataGenContext<Item, CompatBag> ctx, RegistrateRecipeProvider pvd) {
		RecipeGen.unlock(pvd, new ShapedRecipeBuilder(RecipeCategory.MISC, ctx.get(), 1)::unlockedBy, BackpackItems.ENDER_POCKET.get())
				.pattern("DCD").pattern("ABA").pattern(" A ")
				.define('A', Tags.Items.LEATHER).define('B', BackpackItems.ENDER_POCKET)
				.define('D', Items.STRING).define('C', ItemRegistry.SCROLL.get())
				.save(pvd);
	}

}
