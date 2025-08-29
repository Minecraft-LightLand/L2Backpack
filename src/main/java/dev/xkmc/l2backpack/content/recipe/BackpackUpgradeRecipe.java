package dev.xkmc.l2backpack.content.recipe;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.init.data.LBConfig;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2backpack.init.registrate.LBMisc;
import dev.xkmc.l2core.serial.recipe.AbstractSmithingRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec;

import static dev.xkmc.l2backpack.content.backpack.BackpackItem.MAX_ROW;

public class BackpackUpgradeRecipe extends AbstractSmithingRecipe<BackpackUpgradeRecipe> {

	public BackpackUpgradeRecipe(Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
		super(template, base, addition, result);
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider pvd) {
		int init;
		if (LBConfig.SERVER.getSpec() instanceof ModConfigSpec spec && spec.isLoaded())
			init = LBConfig.SERVER.initialRows.get();
		else init = LBConfig.SERVER.initialRows.getDefault();
		return LBItems.DC_ROW.set(super.getResultItem(pvd).copy(), init + 1);
	}

	@Override
	public boolean matches(SmithingRecipeInput container, Level level) {
		if (!super.matches(container, level)) return false;
		ItemStack stack = container.getItem(1);
		BaseBagItem bag = (BaseBagItem) stack.getItem();
		return bag.getRows(stack) < MAX_ROW;
	}

	@Override
	public ItemStack assemble(SmithingRecipeInput container, HolderLookup.Provider access) {
		ItemStack stack = super.assemble(container, access);
		BaseBagItem bag = (BaseBagItem) stack.getItem();
		stack.set(LBItems.DC_ROW, bag.getRows(stack) + 1);
		return stack;
	}

	@Override
	public Serializer<BackpackUpgradeRecipe> getSerializer() {
		return LBMisc.RSC_BAG_UPGRADE.get();
	}
}
