package dev.xkmc.l2backpack.init.registrate;

import com.tterrag.registrate.util.entry.MenuEntry;
import dev.xkmc.l2backpack.content.backpack.BackpackMenu;
import dev.xkmc.l2backpack.content.backpack.BackpackScreen;
import dev.xkmc.l2backpack.content.common.BaseOpenableScreen;
import dev.xkmc.l2backpack.content.quickswap.armorswap.ArmorBagMenu;
import dev.xkmc.l2backpack.content.quickswap.armorswap.ArmorSetBagMenu;
import dev.xkmc.l2backpack.content.quickswap.common.PointerScreen;
import dev.xkmc.l2backpack.content.quickswap.handswap.HandswapMenu;
import dev.xkmc.l2backpack.content.quickswap.handswap.HandswapScreen;
import dev.xkmc.l2backpack.content.quickswap.merged.EnderSwitchMenu;
import dev.xkmc.l2backpack.content.quickswap.merged.MultiSwitchMenu;
import dev.xkmc.l2backpack.content.quickswap.merged.MultiSwitchScreen;
import dev.xkmc.l2backpack.content.quickswap.quiver.QuiverMenu;
import dev.xkmc.l2backpack.content.quickswap.scabbard.ScabbardMenu;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestContainer;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ForgeRegistries;

import static dev.xkmc.l2backpack.init.L2Backpack.REGISTRATE;

/**
 * handles container menu
 */
public class BackpackMenus {

	public static final MenuEntry<BackpackMenu> MT_BACKPACK = REGISTRATE.menu("backpack",
					BackpackMenu::fromNetwork,
					() -> BackpackScreen::new)
			.lang(BackpackMenus::getLangKey).register();

	public static final MenuEntry<WorldChestContainer> MT_WORLD_CHEST = REGISTRATE.menu("dimensional_storage",
					WorldChestContainer::fromNetwork,
					() -> WorldChestScreen::new)
			.lang(BackpackMenus::getLangKey).register();

	public static final MenuEntry<QuiverMenu> MT_ARROW = REGISTRATE.menu("arrow_bag",
					QuiverMenu::fromNetwork,
					() -> PointerScreen<QuiverMenu>::new)
			.lang(BackpackMenus::getLangKey).register();

	public static final MenuEntry<ScabbardMenu> MT_TOOL = REGISTRATE.menu("tool_bag",
					ScabbardMenu::fromNetwork,
					() -> PointerScreen<ScabbardMenu>::new)
			.lang(BackpackMenus::getLangKey).register();

	public static final MenuEntry<ArmorBagMenu> MT_ARMOR = REGISTRATE.menu("armor_bag",
					ArmorBagMenu::fromNetwork,
					() -> PointerScreen<ArmorBagMenu>::new)
			.lang(BackpackMenus::getLangKey).register();

	public static final MenuEntry<ArmorSetBagMenu> MT_ARMOR_SET = REGISTRATE.menu("armor_set",
					ArmorSetBagMenu::fromNetwork,
					() -> BaseOpenableScreen<ArmorSetBagMenu>::new)
			.lang(BackpackMenus::getLangKey).register();

	public static final MenuEntry<MultiSwitchMenu> MT_MULTI = REGISTRATE.menu("multi_switch",
					MultiSwitchMenu::fromNetwork,
					() -> MultiSwitchScreen<MultiSwitchMenu>::new)
			.lang(BackpackMenus::getLangKey).register();

	public static final MenuEntry<EnderSwitchMenu> MT_ES = REGISTRATE.menu("ender_switch",
					EnderSwitchMenu::fromNetwork,
					() -> MultiSwitchScreen<EnderSwitchMenu>::new)
			.lang(BackpackMenus::getLangKey).register();

	public static final MenuEntry<HandswapMenu> MT_HAND = REGISTRATE.menu("offhand_toolbox",
					HandswapMenu::fromNetwork,
					() -> HandswapScreen::new)
			.lang(BackpackMenus::getLangKey).register();

	public static void register() {

	}

	public static String getLangKey(MenuType<?> menu) {
		ResourceLocation rl = ForgeRegistries.MENU_TYPES.getKey(menu);
		assert rl != null;
		return "container." + rl.getNamespace() + "." + rl.getPath();
	}

}
