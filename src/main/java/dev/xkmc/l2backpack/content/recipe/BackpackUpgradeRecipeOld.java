package dev.xkmc.l2backpack.content.recipe;

import dev.xkmc.l2backpack.content.backpack.BackpackItem;
import dev.xkmc.l2backpack.init.data.BackpackConfig;
import dev.xkmc.l2backpack.init.registrate.BackpackMisc;
import dev.xkmc.l2library.base.recipe.AbstractOldSmithingRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public class BackpackUpgradeRecipeOld extends AbstractOldSmithingRecipe<BackpackUpgradeRecipeOld> {

	public BackpackUpgradeRecipeOld(ResourceLocation rl, Ingredient left, Ingredient right, ItemStack result) {
		super(rl, left, right, BackpackItem.setRow(result, BackpackConfig.COMMON.initialRows.get() + 1));
	}

	@Override
	public boolean matches(Container container, Level level) {
		if (!super.matches(container, level)) return false;
		return container.getItem(0).getOrCreateTag().getInt("rows") < 6;
	}

	@Override
	public ItemStack assemble(Container container, RegistryAccess access) {
		ItemStack stack = super.assemble(container, access);
		stack.getOrCreateTag().putInt("rows", Math.max(1, stack.getOrCreateTag().getInt("rows")) + 1);
		return stack;
	}

	@Override
	public Serializer<BackpackUpgradeRecipeOld> getSerializer() {
		return BackpackMisc.RSC_BAG_UPGRADE_OLD.get();
	}
}
