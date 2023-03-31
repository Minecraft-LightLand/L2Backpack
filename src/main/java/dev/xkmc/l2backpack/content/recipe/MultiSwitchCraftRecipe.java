package dev.xkmc.l2backpack.content.recipe;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import dev.xkmc.l2backpack.init.registrate.BackpackMisc;
import dev.xkmc.l2library.base.recipe.AbstractShapedRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class MultiSwitchCraftRecipe extends AbstractShapedRecipe<MultiSwitchCraftRecipe> {

	public MultiSwitchCraftRecipe(ResourceLocation rl, String group, int w, int h, NonNullList<Ingredient> ingredients, ItemStack result) {
		super(rl, group, w, h, ingredients, result);
	}

	@Override
	public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
		List<ItemStack> q = null, s = null, a = null, m = null;
		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);
			if (stack.is(BackpackItems.QUIVER.get())) {
				var list = BaseBagItem.getItems(stack);
				if (list.size() == 9) {
					q = list;
				}
			}
			if (stack.is(BackpackItems.SCABBARD.get())) {
				var list = BaseBagItem.getItems(stack);
				if (list.size() == 9) {
					s = list;
				}
			}
			if (stack.is(BackpackItems.ARMOR_SWAP.get())) {

				var list = BaseBagItem.getItems(stack);
				if (list.size() == 9) {
					a = list;
				}
			}
			if (stack.is(BackpackItems.MULTI_SWITCH.get())) {
				var list = BaseBagItem.getItems(stack);
				if (list.size() == 27) {
					m = list;
				}
			}
		}
		if (m == null) {
			m = new ArrayList<>(27);
			for (int i = 0; i < 27; i++) {
				m.add(i, ItemStack.EMPTY);
			}
			if (q != null) {
				for (int i = 0; i < 9; i++) {
					m.set(i, q.get(i));
				}
			}
			if (s != null) {
				for (int i = 0; i < 9; i++) {
					m.set(i + 9, s.get(i));
				}
			}
			if (a != null) {
				for (int i = 0; i < 9; i++) {
					m.set(i + 18, a.get(i));
				}
			}
		}
		ItemStack ans = super.assemble(container, access);
		BaseBagItem.setItems(ans, m);
		return ans;
	}

	@Override
	public Serializer<MultiSwitchCraftRecipe> getSerializer() {
		return BackpackMisc.RSC_BAG_CRAFT.get();
	}

}
