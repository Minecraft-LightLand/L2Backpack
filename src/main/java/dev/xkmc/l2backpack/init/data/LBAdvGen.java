package dev.xkmc.l2backpack.init.data;

import com.tterrag.registrate.providers.RegistrateAdvancementProvider;
import dev.xkmc.l2backpack.compat.PatchouliCompat;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.advancement.BagInteractTrigger;
import dev.xkmc.l2backpack.init.advancement.DrawerInteractTrigger;
import dev.xkmc.l2backpack.init.advancement.SlotClickTrigger;
import dev.xkmc.l2backpack.init.registrate.LBBlocks;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2backpack.init.registrate.LBMisc;
import dev.xkmc.l2backpack.init.registrate.LBTriggers;
import dev.xkmc.l2backpack.network.ClickInteractToServer;
import dev.xkmc.l2core.serial.advancements.AdvancementGenerator;
import dev.xkmc.l2core.serial.advancements.CriterionBuilder;
import dev.xkmc.l2menustacker.screen.base.L2MSReg;
import dev.xkmc.l2menustacker.screen.triggers.ExitMenuTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.Optional;

import static dev.xkmc.l2backpack.content.backpack.BackpackItem.MAX_ROW;

public class LBAdvGen {

	public static void genAdvancements(RegistrateAdvancementProvider pvd) {
		pvd.accept(Advancement.Builder.advancement().addCriterion("locate",
						PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location()))
				.build(L2Backpack.loc("detection")));
		AdvancementGenerator gen = new AdvancementGenerator(pvd, L2Backpack.MODID);
		gen.new TabBuilder("backpacks").root("root", backpack(DyeColor.WHITE),
						CriterionBuilder.one(PlayerTrigger.TriggerInstance.tick()),
						"Welcome to L2Backpack", "Guide to Backpacks")
				.root().patchouli(L2Backpack.REGISTRATE,
						CriterionBuilder.one(PlayerTrigger.TriggerInstance.tick()),
						PatchouliCompat.HELPER,
						"Intro to Backpacks", "Read the backpack guide")
				.root().create("backpack", backpack(DyeColor.RED),
						CriterionBuilder.item(LBTagGen.BACKPACKS),
						"Your First Backpack!", "Obtain a Backpack")
				.create("press_b", backpack(DyeColor.CYAN),
						CriterionBuilder.one(SlotClickTrigger.fromKeyBind().build()),
						"Keybind", "open Backpack through keybind")
				.create("quick_access", backpack(DyeColor.BLUE),
						CriterionBuilder.one(SlotClickTrigger.fromGUI().build()),
						"Quick Access", "open Backpack in inventory directly")
				.create("folder_structure", backpack(DyeColor.LIGHT_BLUE),
						CriterionBuilder.one(ExitMenuTrigger.exitOne().build()),
						"Folders?", "exit Backpack GUI and return to previous page")
				.create("close_all", backpack(DyeColor.LIME),
						CriterionBuilder.one(ExitMenuTrigger.exitAll().build()),
						"Close All at Once", "exit all GUIs at once using shift+esc")

				// ender backpack
				.root().enter().create("ender", LBItems.ENDER_BACKPACK.get(),
						CriterionBuilder.item(LBItems.ENDER_BACKPACK.get()),
						"Portable Ender Chest", "Obtain an Ender Backpack")
				.create("safe_storage", backpack(DyeColor.PURPLE),
						CriterionBuilder.one(SlotClickTrigger.fromBackpack(L2MSReg.IS_ENDER.get()).build()),
						"GameRule KeepInventory True", "Open a Backpack in Ender Backpack")
				.create("color", backpack(DyeColor.GREEN),
						CriterionBuilder.one(InventoryChangeTrigger.TriggerInstance.hasItems(
								Arrays.stream(LBItems.BACKPACKS).map(e -> (ItemLike) e.get())
										.toArray(ItemLike[]::new))),
						"Colorful Inventory", "Obtain Backpacks of all colors").type(AdvancementType.CHALLENGE)

				//interact
				.root().enter().create("interact_load", LBItems.DRAWER.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.LOAD).build()),
						"Fast Transfer", "Load things into a Drawer by shift-left clicking a chest with it")
				.create("interact_dump", LBItems.DRAWER.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.DUMP).build()),
						"Dump Out", "Dump things into a chest by shift-right clicking a chest with a Drawer")

				// ender pocket
				.root().create("ender_pocket", LBItems.ENDER_POCKET.get(),
						CriterionBuilder.item(LBItems.ENDER_POCKET.get()),
						"4D Pocket", "Obtain an Ender Pocket")
				.create("upgrade", backpack(DyeColor.LIGHT_GRAY),
						CriterionBuilder.item(LBTagGen.BACKPACKS,
								DataComponentPredicate.builder().expect(LBItems.DC_ROW.get(), 3).build()),
						"Expand the Space", "Upgrade a Backpack")
				.create("upgrade_max", backpack(DyeColor.GRAY),
						CriterionBuilder.item(LBTagGen.BACKPACKS,
								DataComponentPredicate.builder().expect(LBItems.DC_ROW.get(), MAX_ROW).build()),
						"Maximize the Space", "Upgrade a Backpack to max level").type(AdvancementType.CHALLENGE)

				// dimensional backpack
				.root().enter().create("dimension", dimension(DyeColor.WHITE),
						CriterionBuilder.item(LBTagGen.DIMENSIONAL_STORAGES),
						"Another Ender Chest?", "Obtain a Dimensional Backpack")
				.create("dimension_recursion", dimension(DyeColor.YELLOW),
						CriterionBuilder.one(SlotClickTrigger.fromBackpack(LBMisc.IS_DIM.get()).build()),
						"Infinite Recursion", "Open a Backpack in Dimensional Backpack").type(AdvancementType.GOAL)
				.create("dimension_hopper", dimension(DyeColor.LIGHT_GRAY),
						CriterionBuilder.one(LBTriggers.REMOTE.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()))),
						"Ender Chest with Hopper", "Use Hopper to insert items into a Dimensional Backpack or an Ender Drawer").type(AdvancementType.GOAL)
				.create("dimension_analog", dimension(DyeColor.RED),
						CriterionBuilder.one(LBTriggers.ANALOG.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()))),
						"Ender Chest with Comparator", "Use a Comparator to measure a Dimensional Backpack or an Ender Drawer").type(AdvancementType.CHALLENGE)

				// drawer
				.root().enter().create("drawer", LBItems.DRAWER.get(),
						CriterionBuilder.item(LBItems.DRAWER.get()),
						"Portable Drawer", "Obtain a Drawer")
				.create("drawer_store", LBItems.DRAWER.get(),
						CriterionBuilder.one(DrawerInteractTrigger.fromType(ClickInteractToServer.Type.INSERT).build()),
						"Is it a Stack?", "Put items into a Drawer")
				.create("drawer_take", LBItems.DRAWER.get(),
						CriterionBuilder.one(DrawerInteractTrigger.fromType(ClickInteractToServer.Type.TAKE).build()),
						"It is a Stack", "Take items from a Drawer")
				.create("drawer_collect", LBItems.DRAWER.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.COLLECT,
								LBItems.DRAWER.get(), LBItems.ENDER_DRAWER.get()).build()),
						"Bye bye Cobblestone", "Collect items into drawer by shift-right clicking with it")
				.create("drawer_extract", LBItems.DRAWER.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.EXTRACT,
								LBItems.DRAWER.get(), LBItems.ENDER_DRAWER.get()).build()),
						"Come Here Cobblestone", "Takes items from drawer by right clicking with it")

				// ender drawer
				.root().enter().enter().create("ender_drawer", LBItems.ENDER_DRAWER.get(),
						CriterionBuilder.item(LBItems.ENDER_DRAWER.get()),
						"A Third Ender Chest?", "Obtain an Ender Drawer")
				.create("ender_drawer_place", LBItems.ENDER_DRAWER.get(),
						CriterionBuilder.one(ItemUsedOnLocationTrigger.TriggerInstance
								.placedBlock(LBBlocks.ENDER_DRAWER.get())),
						"Remote Logistics", "Place down an Ender Drawer").type(AdvancementType.GOAL)
				.create("dimension_share", dimension(DyeColor.BLUE),
						CriterionBuilder.one(LBTriggers.SHARE.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()))),
						"Shared Drive", "Open a Dimensional Backpack or use an Ender Drawer that belongs to someone else").type(AdvancementType.CHALLENGE)

				// bags
				.root().create("bag", LBItems.ARMOR_BAG.get(),
						CriterionBuilder.items(LBItems.ARMOR_BAG.get(), LBItems.BOOK_BAG.get()),
						"Make Unstackables Stackable", "Obtain an Armor Bag or Book Bag")
				.create("bag_collect", LBItems.ARMOR_BAG.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.COLLECT,
								LBItems.ARMOR_BAG.get(), LBItems.BOOK_BAG.get()).build()),
						"Take the Loot", "Store unused weapons and tools into bag by right clicking with it")
				.create("bag_dump", LBItems.ARMOR_BAG.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.EXTRACT,
								LBItems.ARMOR_BAG.get(), LBItems.BOOK_BAG.get()).build()),
						"Throw out the Loot", "Throw out collected weapons and tools into bag by shift-right clicking with it")

				//quiver
				.root().create("quiver", LBItems.QUIVER.get(),
						CriterionBuilder.item(LBItems.QUIVER.get()),
						"9 Arrows on Bow", "Obtain a Quiver")
				.create("scabbard", LBItems.SCABBARD.get(),
						CriterionBuilder.item(LBItems.SCABBARD.get()),
						"9 Tools in One", "Obtain a Tool Swap")
				.create("armor_swap", LBItems.ARMOR_SWAP.get(),
						CriterionBuilder.item(LBItems.ARMOR_SWAP.get()),
						"Backup Armors", "Obtain an Armor Swap")
				.finish();

	}

	private static Item backpack(DyeColor color) {
		return LBItems.BACKPACKS[color.ordinal()].get();
	}

	private static Item dimension(DyeColor color) {
		return LBItems.DIMENSIONAL_STORAGE[color.ordinal()].get();
	}

}
