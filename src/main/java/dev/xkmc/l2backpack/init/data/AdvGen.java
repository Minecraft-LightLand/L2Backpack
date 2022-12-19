package dev.xkmc.l2backpack.init.data;

import dev.xkmc.l2backpack.content.backpack.BackpackItem;
import dev.xkmc.l2backpack.content.common.ContainerType;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.advancement.*;
import dev.xkmc.l2backpack.init.advancement.util.AdvancementGenerator;
import dev.xkmc.l2backpack.init.advancement.util.CriterionBuilder;
import dev.xkmc.l2backpack.init.registrate.BackpackBlocks;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import dev.xkmc.l2backpack.network.drawer.DrawerInteractToServer;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateAdvancementProvider;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;

import java.util.Arrays;

public class AdvGen {

	public static void genAdvancements(RegistrateAdvancementProvider pvd) {
		AdvancementGenerator gen = new AdvancementGenerator(pvd, L2Backpack.MODID);
		gen.new TabBuilder("backpacks").root("root", backpack(DyeColor.WHITE),
						CriterionBuilder.item(Tags.Items.CHESTS),
						"Welcome to L2Backpack", "Guide to Backpacks")
				.root().create("backpack", backpack(DyeColor.RED),
						CriterionBuilder.item(ItemTags.BACKPACKS.tag),
						"Your First Backpack!", "Obtain a Backpack")
				.create("press_b", backpack(DyeColor.CYAN),
						CriterionBuilder.one(SlotClickTrigger.fromKeyBind()),
						"Keybind", "open Backpack through keybind")
				.create("quick_access", backpack(DyeColor.BLUE),
						CriterionBuilder.one(SlotClickTrigger.fromGUI()),
						"Quick Access", "open Backpack in inventory directly")
				.create("folder_structure", backpack(DyeColor.LIGHT_BLUE),
						CriterionBuilder.one(ExitMenuTrigger.exitOne()),
						"Folders?", "exit Backpack GUI and return to previous page")
				.create("close_all", backpack(DyeColor.LIME),
						CriterionBuilder.one(ExitMenuTrigger.exitAll()),
						"Close All at Once", "exit all GUIs at once using shift+esc")

				// ender backpack
				.root().enter().create("ender", BackpackItems.ENDER_BACKPACK.get(),
						CriterionBuilder.item(BackpackItems.ENDER_BACKPACK.get()),
						"Portable Ender Chest", "Obtain an Ender Backpack")
				.create("safe_storage", backpack(DyeColor.PURPLE),
						CriterionBuilder.one(SlotClickTrigger.fromBackpack(ContainerType.ENDER)),
						"GameRule KeepInventory True", "Open a Backpack in Ender Backpack")
				.create("color", backpack(DyeColor.GREEN),
						CriterionBuilder.one(InventoryChangeTrigger.TriggerInstance.hasItems(
								Arrays.stream(BackpackItems.BACKPACKS).map(e -> (ItemLike) e.get())
										.toArray(ItemLike[]::new))),
						"Colorful Inventory", "Obtain Backpacks of all colors").type(FrameType.CHALLENGE)

				//interact
				.root().enter().create("interact_load", BackpackItems.DRAWER.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.LOAD)),
						"Fast Transfer", "Load things into a Drawer by shift-left clicking a chest with it")
				.create("interact_dump", BackpackItems.DRAWER.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.DUMP)),
						"Dump Out", "Dump things into a chest by shift-right clicking a chest with a Drawer")

				// ender pocket
				.root().create("ender_pocket", BackpackItems.ENDER_POCKET.get(),
						CriterionBuilder.item(BackpackItems.ENDER_POCKET.get()),
						"4D Pocket", "Obtain an Ender Pocket")
				.create("upgrade", backpack(DyeColor.LIGHT_GRAY),
						CriterionBuilder.item(ItemTags.BACKPACKS.tag,
								BackpackItem.setRow(backpack(DyeColor.WHITE)
										.getDefaultInstance(), 3).getOrCreateTag()),
						"Expand the Space", "Upgrade a Backpack")
				.create("upgrade_max", backpack(DyeColor.GRAY),
						CriterionBuilder.item(ItemTags.BACKPACKS.tag,
								BackpackItem.setRow(backpack(DyeColor.WHITE)
										.getDefaultInstance(), 6).getOrCreateTag()),
						"Maximize the Space", "Upgrade a Backpack to max level").type(FrameType.CHALLENGE)

				// dimensional backpack
				.root().enter().create("dimension", dimension(DyeColor.WHITE),
						CriterionBuilder.item(ItemTags.DIMENSIONAL_STORAGES.tag),
						"Another Ender Chest?", "Obtain a Dimensional Backpack")
				.create("dimension_recursion", dimension(DyeColor.YELLOW),
						CriterionBuilder.one(SlotClickTrigger.fromBackpack(ContainerType.DIMENSION)),
						"Infinite Recursion", "Open a Backpack in Dimensional Backpack").type(FrameType.GOAL)
				.create("dimension_hopper", dimension(DyeColor.LIGHT_GRAY),
						CriterionBuilder.one(RemoteHopperTrigger.ins()),
						"Ender Chest with Hopper", "Use Hopper to insert items into a Dimensional Backpack or an Ender Drawer").type(FrameType.GOAL)
				.create("dimension_analog", dimension(DyeColor.RED),
						CriterionBuilder.one(AnalogSignalTrigger.ins()),
						"Ender Chest with Comparator", "Use a Comparator to measure a Dimensional Backpack or an Ender Drawer").type(FrameType.CHALLENGE)

				// drawer
				.root().enter().create("drawer", BackpackItems.DRAWER.get(),
						CriterionBuilder.item(BackpackItems.DRAWER.get()),
						"Portable Drawer", "Obtain a Drawer")
				.create("drawer_store", BackpackItems.DRAWER.get(),
						CriterionBuilder.one(DrawerInteractTrigger.fromType(DrawerInteractToServer.Type.INSERT)),
						"Is it a Stack?", "Put items into a Drawer")
				.create("drawer_take", BackpackItems.DRAWER.get(),
						CriterionBuilder.one(DrawerInteractTrigger.fromType(DrawerInteractToServer.Type.TAKE)),
						"It is a Stack", "Take items from a Drawer")
				.create("drawer_collect", BackpackItems.DRAWER.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.COLLECT,
								BackpackItems.DRAWER.get(), BackpackItems.ENDER_DRAWER.get())),
						"Bye bye Cobblestone", "Collect items into drawer by shift-right clicking with it")
				.create("drawer_extract", BackpackItems.DRAWER.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.EXTRACT,
								BackpackItems.DRAWER.get(), BackpackItems.ENDER_DRAWER.get())),
						"Come Here Cobblestone", "Takes items from drawer by right clicking with it")

				// ender drawer
				.root().enter().enter().create("ender_drawer", BackpackItems.ENDER_DRAWER.get(),
						CriterionBuilder.item(BackpackItems.ENDER_DRAWER.get()),
						"A Third Ender Chest?", "Obtain an Ender Drawer")
				.create("ender_drawer_place", BackpackItems.ENDER_DRAWER.get(),
						CriterionBuilder.one(PlacedBlockTrigger.TriggerInstance
								.placedBlock(BackpackBlocks.ENDER_DRAWER.get())),
						"Remote Logistics", "Place down an Ender Drawer").type(FrameType.GOAL)
				.create("dimension_share", dimension(DyeColor.BLUE),
						CriterionBuilder.or().add(SlotClickTrigger.fromOthers()).add(DrawerInteractTrigger.fromOthers()),
						"Shared Drive", "Open a Dimensional Backpack or use an Ender Drawer that belongs to someone else").type(FrameType.CHALLENGE)

				// bags
				.root().create("bag", BackpackItems.ARMOR_BAG.get(),
						CriterionBuilder.items(BackpackItems.ARMOR_BAG.get(), BackpackItems.BOOK_BAG.get()),
						"Make Unstackables Stackable", "Obtain an Armor Bag or Book Bag")
				.create("bag_collect", BackpackItems.ARMOR_BAG.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.COLLECT,
								BackpackItems.ARMOR_BAG.get(), BackpackItems.BOOK_BAG.get())),
						"Take the Loot", "Store unused weapons and tools into bag by right clicking with it")
				.create("bag_dump", BackpackItems.ARMOR_BAG.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.EXTRACT,
								BackpackItems.ARMOR_BAG.get(), BackpackItems.BOOK_BAG.get())),
						"Throw out the Loot", "Throw out collected weapons and tools into bag by shift-right clicking with it")

				//quiver
				.root().create("quiver", BackpackItems.QUIVER.get(),
						CriterionBuilder.item(BackpackItems.QUIVER.get()),
						"9 Arrows on Bow", "Obtain a Quiver")
				.create("scabbard", BackpackItems.SCABBARD.get(),
						CriterionBuilder.item(BackpackItems.SCABBARD.get()),
						"9 Tools in One", "Obtain a Tool Swap")
				.create("armor_swap", BackpackItems.ARMOR_SWAP.get(),
						CriterionBuilder.item(BackpackItems.ARMOR_SWAP.get()),
						"Backup Armors", "Obtain an Armor Swap")
				.create("multi_switch", BackpackItems.MULTI_SWITCH.get(),
						CriterionBuilder.item(BackpackItems.MULTI_SWITCH.get()),
						"Three in One", "Obtain an Combined Swap").type(FrameType.GOAL)
				.create("ender_switch", BackpackItems.ENDER_SWITCH.get(),
						CriterionBuilder.item(BackpackItems.ENDER_SWITCH.get()),
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
