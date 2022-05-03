package dev.xkmc.l2backpack.init.registrate;

import dev.xkmc.l2library.repack.registrate.util.entry.MenuEntry;
import dev.xkmc.l2backpack.content.backpack.BackpackContainer;
import dev.xkmc.l2backpack.content.backpack.BackpackScreen;
import dev.xkmc.l2backpack.content.worldchest.WorldChestContainer;
import dev.xkmc.l2backpack.content.worldchest.WorldChestScreen;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.inventory.MenuType;

import static dev.xkmc.l2backpack.init.L2Backpack.REGISTRATE;

/**
 * handles container menu
 */
@MethodsReturnNonnullByDefault
public class LightlandMenu {

	public static final MenuEntry<BackpackContainer> MT_BACKPACK = REGISTRATE.menu("backpack",
			BackpackContainer::fromNetwork, () -> BackpackScreen::new).lang(LightlandMenu::getLangKey).register();
	public static final MenuEntry<WorldChestContainer> MT_WORLD_CHEST = REGISTRATE.menu("dimensional_storage",
			WorldChestContainer::fromNetwork, () -> WorldChestScreen::new).lang(LightlandMenu::getLangKey).register();

	public static void register() {

	}

	public static String getLangKey(MenuType<?> menu) {
		return "container." + menu.getRegistryName().getNamespace() + "." + menu.getRegistryName().getPath();
	}

}
