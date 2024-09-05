package dev.xkmc.l2backpack.init.registrate;

import com.tterrag.registrate.util.entry.MenuEntry;
import dev.xkmc.l2backpack.content.backpack.BackpackMenu;
import dev.xkmc.l2backpack.content.backpack.BackpackScreen;
import dev.xkmc.l2backpack.content.common.BaseOpenableScreen;
import dev.xkmc.l2backpack.content.quickswap.common.GenericSwapMenu;
import dev.xkmc.l2backpack.content.quickswap.set.ArmorSetBagMenu;
import dev.xkmc.l2backpack.content.remote.dimensional.DimensionalContainer;
import dev.xkmc.l2backpack.content.remote.dimensional.DimensionalScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

import static dev.xkmc.l2backpack.init.L2Backpack.REGISTRATE;

/**
 * handles container menu
 */
public class LBMenu {

	public static final MenuEntry<BackpackMenu> MT_BACKPACK = REGISTRATE.menu("backpack",
					BackpackMenu::fromNetwork,
					() -> BackpackScreen::new)
			.lang(LBMenu::getLangKey).register();

	public static final MenuEntry<DimensionalContainer> MT_WORLD_CHEST = REGISTRATE.menu("dimensional_storage",
					DimensionalContainer::fromNetwork,
					() -> DimensionalScreen::new)
			.lang(LBMenu::getLangKey).register();

	public static final MenuEntry<GenericSwapMenu> MT_9 = REGISTRATE.menu("generic_swap",
					GenericSwapMenu::fromNetwork,
					() -> BaseOpenableScreen<GenericSwapMenu>::new)
			.register();

	public static final MenuEntry<ArmorSetBagMenu> MT_ARMOR_SET = REGISTRATE.menu("armor_set",
					ArmorSetBagMenu::fromNetwork,
					() -> BaseOpenableScreen<ArmorSetBagMenu>::new)
			.lang(LBMenu::getLangKey).register();

	public static void register() {

	}

	public static String getLangKey(MenuType<?> menu) {
		ResourceLocation rl = BuiltInRegistries.MENU.getKey(menu);
		assert rl != null;
		return "container." + rl.getNamespace() + "." + rl.getPath();
	}

}
