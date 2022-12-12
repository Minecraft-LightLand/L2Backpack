package dev.xkmc.l2backpack.content.recipe;

import dev.xkmc.l2backpack.init.data.ItemTags;
import dev.xkmc.l2backpack.init.registrate.BackpackRecipe;
import dev.xkmc.l2library.base.recipe.AbstractShapelessRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class BackpackDyeRecipe extends AbstractShapelessRecipe<BackpackDyeRecipe> {

	public BackpackDyeRecipe(ResourceLocation rl, String group, ItemStack result, NonNullList<Ingredient> ingredients) {
		super(rl, group, result, ingredients);
	}

	@Override
	public ItemStack assemble(CraftingContainer container) {
		ItemStack bag = ItemStack.EMPTY;
		for (int i = 0; i < container.getContainerSize(); i++) {
			if (ItemTags.BACKPACKS.matches(container.getItem(i))) {
				bag = container.getItem(i);
			}
		}
		ItemStack stack = super.assemble(container);
		stack.setTag(bag.getTag());
		return stack;
	}

	@Override
	public Serializer<BackpackDyeRecipe> getSerializer() {
		return BackpackRecipe.RSC_BAG_DYE.get();
	}
}
