package dev.xkmc.l2backpack.init.registrate;

import dev.xkmc.l2backpack.content.arrowbag.ArrowBagContainer;
import dev.xkmc.l2backpack.content.arrowbag.ArrowBagScreen;
import dev.xkmc.l2backpack.content.backpack.BackpackContainer;
import dev.xkmc.l2backpack.content.backpack.BackpackScreen;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestContainer;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestScreen;
import dev.xkmc.l2library.repack.registrate.util.entry.MenuEntry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ForgeRegistries;

import static dev.xkmc.l2backpack.init.L2Backpack.REGISTRATE;

/**
 * handles container menu
 */
@MethodsReturnNonnullByDefault
public class BackpackMenu {

	public static final MenuEntry<BackpackContainer> MT_BACKPACK = REGISTRATE.menu("backpack",
			BackpackContainer::fromNetwork, () -> BackpackScreen::new).lang(BackpackMenu::getLangKey).register();
	public static final MenuEntry<WorldChestContainer> MT_WORLD_CHEST = REGISTRATE.menu("dimensional_storage",
			WorldChestContainer::fromNetwork, () -> WorldChestScreen::new).lang(BackpackMenu::getLangKey).register();
	public static final MenuEntry<ArrowBagContainer> MT_ARROW = REGISTRATE.menu("arrow_bag",
			ArrowBagContainer::fromNetwork, () -> ArrowBagScreen::new).lang(BackpackMenu::getLangKey).register();

	public static void register() {

	}

	public static String getLangKey(MenuType<?> menu) {
		ResourceLocation rl = ForgeRegistries.CONTAINERS.getKey(menu);
		return "container." + rl.getNamespace() + "." + rl.getPath();
	}

}
