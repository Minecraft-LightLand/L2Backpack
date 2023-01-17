package dev.xkmc.l2backpack.init.registrate;

import com.tterrag.registrate.util.entry.MenuEntry;
import dev.xkmc.l2backpack.content.backpack.BackpackContainer;
import dev.xkmc.l2backpack.content.backpack.BackpackScreen;
import dev.xkmc.l2backpack.content.common.BaseOpenableScreen;
import dev.xkmc.l2backpack.content.quickswap.armorswap.ArmorBagContainer;
import dev.xkmc.l2backpack.content.quickswap.merged.EnderSwitchContainer;
import dev.xkmc.l2backpack.content.quickswap.merged.MultiSwitchContainer;
import dev.xkmc.l2backpack.content.quickswap.merged.MultiSwitchScreen;
import dev.xkmc.l2backpack.content.quickswap.quiver.QuiverContainer;
import dev.xkmc.l2backpack.content.quickswap.scabbard.ScabbardContainer;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestContainer;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ForgeRegistries;

import static dev.xkmc.l2backpack.init.L2Backpack.REGISTRATE;

/**
 * handles container menu
 */
public class BackpackMenu {

	public static final MenuEntry<BackpackContainer> MT_BACKPACK = REGISTRATE.menu("backpack",
					BackpackContainer::fromNetwork,
					() -> BackpackScreen::new)
			.lang(BackpackMenu::getLangKey).register();

	public static final MenuEntry<WorldChestContainer> MT_WORLD_CHEST = REGISTRATE.menu("dimensional_storage",
					WorldChestContainer::fromNetwork,
					() -> WorldChestScreen::new)
			.lang(BackpackMenu::getLangKey).register();

	public static final MenuEntry<QuiverContainer> MT_ARROW = REGISTRATE.menu("arrow_bag",
					QuiverContainer::fromNetwork,
					() -> BaseOpenableScreen<QuiverContainer>::new)
			.lang(BackpackMenu::getLangKey).register();

	public static final MenuEntry<ScabbardContainer> MT_TOOL = REGISTRATE.menu("tool_bag",
					ScabbardContainer::fromNetwork,
					() -> BaseOpenableScreen<ScabbardContainer>::new)
			.lang(BackpackMenu::getLangKey).register();

	public static final MenuEntry<ArmorBagContainer> MT_ARMOR = REGISTRATE.menu("armor_bag",
					ArmorBagContainer::fromNetwork,
					() -> BaseOpenableScreen<ArmorBagContainer>::new)
			.lang(BackpackMenu::getLangKey).register();

	public static final MenuEntry<MultiSwitchContainer> MT_MULTI = REGISTRATE.menu("multi_switch",
					MultiSwitchContainer::fromNetwork,
					() -> MultiSwitchScreen<MultiSwitchContainer>::new)
			.lang(BackpackMenu::getLangKey).register();

	public static final MenuEntry<EnderSwitchContainer> MT_ES = REGISTRATE.menu("ender_switch",
					EnderSwitchContainer::fromNetwork,
					() -> MultiSwitchScreen<EnderSwitchContainer>::new)
			.lang(BackpackMenu::getLangKey).register();

	public static void register() {

	}

	public static String getLangKey(MenuType<?> menu) {
		ResourceLocation rl = ForgeRegistries.MENU_TYPES.getKey(menu);
		assert rl != null;
		return "container." + rl.getNamespace() + "." + rl.getPath();
	}

}
