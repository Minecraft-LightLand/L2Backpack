package dev.xkmc.l2backpack.compat.bags;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.socket.gem.GemItem;
import dev.xkmc.l2backpack.init.data.LBRecipeGen;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class ApothProxy {

	public static boolean pred(ItemStack stack) {
		return stack.getItem() instanceof GemItem;
	}

	public static void recipe(DataGenContext<Item, CompatBag> ctx, RegistrateRecipeProvider pvd) {
		LBRecipeGen.unlock(pvd, new ShapedRecipeBuilder(RecipeCategory.MISC, ctx.get(), 1)::unlockedBy, LBItems.ENDER_POCKET.get())
				.pattern("DCD").pattern("ABA").pattern(" A ")
				.define('A', Tags.Items.LEATHERS).define('B', LBItems.ENDER_POCKET)
				.define('D', Items.STRING).define('C', Apoth.Items.GEM_DUST.value())
				.save(pvd);
	}

}
