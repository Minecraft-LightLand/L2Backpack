package dev.xkmc.l2backpack.init.data;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.advancement.AdvancementGenerator;
import dev.xkmc.l2backpack.init.advancement.CriterionBuilder;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateAdvancementProvider;
import net.minecraft.advancements.FrameType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class AdvGen {

	public static void genAdvancements(RegistrateAdvancementProvider pvd) {
		AdvancementGenerator gen = new AdvancementGenerator(pvd, L2Backpack.MODID);
		gen.new TabBuilder("backpacks").root("root", backpack(DyeColor.WHITE),
						CriterionBuilder.item(Items.CHEST),
						"Welcome to L2Backpack", "Guide to Backpacks")
				.root().create("backpack", backpack(DyeColor.RED),
						CriterionBuilder.item(ItemTags.BACKPACKS.tag),
						"Your First Backpack!", "Obtain a Backpack")
				.root().enter().create("press_b", backpack(DyeColor.CYAN),
						CriterionBuilder.none(),
						"Keybind", "open Backpack through keybind")
				.create("quick_access", backpack(DyeColor.BLUE),
						CriterionBuilder.none(),
						"Quick Access", "open Backpack in inventory directly")
				.create("folder_structure", backpack(DyeColor.LIGHT_BLUE),
						CriterionBuilder.none(),
						"Folders?", "exit Backpack GUI and return to previous page")
				.create("close_all", backpack(DyeColor.LIME),
						CriterionBuilder.none(),
						"Close All at Once", "exit all GUIs at once using shift+esc")

				// ender backpack
				.root().enter().create("ender", BackpackItems.ENDER_BACKPACK.get(),
						CriterionBuilder.none(),
						"Portable Ender Chest", "Obtain an Ender Backpack")
				.create("safe_storage", backpack(DyeColor.PURPLE),
						CriterionBuilder.none(),
						"GameRule KeepInventory True", "Open a Backpack in Ender Backpack")
				.create("color", backpack(DyeColor.GREEN),
						CriterionBuilder.none(),
						"Colorful Inventory", "Obtain Backpacks of all colors").type(FrameType.CHALLENGE)

				//interact
				.root().enter().create("interact_load", BackpackItems.DRAWER.get(),
						CriterionBuilder.none(),
						"Fast Transfer", "Load things into a Drawer by shift-left clicking a chest with it")
				.create("interact_dump", BackpackItems.DRAWER.get(),
						CriterionBuilder.none(),
						"Dump Out", "Dump things into a chest by shift-right clicking a chest with a Drawer")

				// ender pocket
				.root().create("ender_pocket", BackpackItems.ENDER_POCKET.get(),
						CriterionBuilder.none(),
						"4D Pocket", "Obtain an Ender Pocket")
				.root().enter().create("upgrade", backpack(DyeColor.LIGHT_GRAY),
						CriterionBuilder.none(),
						"Expand the Space", "Upgrade a Backpack")
				.root().enter().enter().create("upgrade_max", backpack(DyeColor.GRAY),
						CriterionBuilder.none(),
						"Maximize the Space", "Upgrade a Backpack to max level").type(FrameType.CHALLENGE)

				// dimensional backpack
				.root().enter().create("dimension", dimension(DyeColor.WHITE),
						CriterionBuilder.none(),
						"Another Ender Chest?", "Obtain a Dimensional Backpack")
				.create("dimension_hopper", dimension(DyeColor.LIGHT_GRAY),
						CriterionBuilder.none(),
						"Ender Chest with Hopper", "Use Hopper to insert items into Dimensional Backpack")
				.create("dimension_recursion", dimension(DyeColor.YELLOW),
						CriterionBuilder.none(),
						"Infinite Recursion", "Open a Backpack in Dimensional Backpack")
				.create("dimension_share", dimension(DyeColor.RED),
						CriterionBuilder.none(),
						"Shared Drive", "Open a Dimensional Backpack that belongs to someone else")

				// drawer
				.root().enter().create("drawer", BackpackItems.DRAWER.get(),
						CriterionBuilder.none(),
						"Portable Drawer", "Obtain a Drawer")
				.root().enter().enter().create("drawer_store", BackpackItems.DRAWER.get(),
						CriterionBuilder.none(),
						"Is it a Stack?", "Put items into a Drawer")
				.create("drawer_take", BackpackItems.DRAWER.get(),
						CriterionBuilder.none(),
						"It is a Stack", "Take items from a Drawer")
				.create("drawer_collect", BackpackItems.DRAWER.get(),
						CriterionBuilder.none(),
						"Bye bye Cobblestone", "Collect items into drawer by shift-right clicking with it")
				.create("drawer_extract", BackpackItems.DRAWER.get(),
						CriterionBuilder.none(),
						"Come Here Cobblestone", "Takes items from drawer by right clicking with it")
				.root().enter().enter().create("ender_drawer", BackpackItems.ENDER_DRAWER.get(),
						CriterionBuilder.none(),
						"A Third Ender Chest?", "Obtain an Ender Drawer")
				.create("ender_drawer_view", BackpackItems.ENDER_DRAWER.get(),
						CriterionBuilder.none(),
						"Remote Logistics", "Place down an Ender Drawer")

				// bags
				.root().create("bag", BackpackItems.ARMOR_BAG.get(),
						CriterionBuilder.none(),
						"Make Unstackables Stackable", "Obtain an Armor Bag or Book Bag")
				.create("bag_collect", BackpackItems.ARMOR_BAG.get(),
						CriterionBuilder.none(),
						"Take the Loot", "Store unused weapons and tools into bag by right clicking with it")
				.create("bag_dump", BackpackItems.ARMOR_BAG.get(),
						CriterionBuilder.none(),
						"Throw out the Loot", "Throw out collected weapons and tools into bag by shift-right clicking with it")
				.create("bag_full", BackpackItems.ARMOR_BAG.get(),
						CriterionBuilder.none(),
						"Fill the Bag", "Fill up the Bag").type(FrameType.GOAL)

				//quiver
				.root().create("quiver", BackpackItems.QUIVER.get(),
						CriterionBuilder.none(),
						"9 Arrows on Bow", "Obtain a Quiver")
				.create("scabbard", BackpackItems.SCABBARD.get(),
						CriterionBuilder.none(),
						"9 Tools in One", "Obtain a Tool Swap")
				.create("armor_swap", BackpackItems.ARMOR_SWAP.get(),
						CriterionBuilder.none(),
						"Backup Armors", "Obtain an Armor Swap")
				.create("multi_switch", BackpackItems.MULTI_SWITCH.get(),
						CriterionBuilder.none(),
						"Three in One", "Obtain an Combined Swap").type(FrameType.GOAL)
				.create("ender_switch", BackpackItems.ENDER_SWITCH.get(),
						CriterionBuilder.none(),
						"Four in One", "Obtain an Ender Swap").type(FrameType.CHALLENGE)
				.finish();

	}

	private static Item backpack(DyeColor color) {
		return BackpackItems.BACKPACKS[color.ordinal()].get();
	}

	private static Item dimension(DyeColor color) {
		return BackpackItems.DIMENSIONAL_STORAGE[color.ordinal()].get();
	}

}
