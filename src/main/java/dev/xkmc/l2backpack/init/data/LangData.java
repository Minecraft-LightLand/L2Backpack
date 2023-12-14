package dev.xkmc.l2backpack.init.data;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.loot.LootGen;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraftforge.fml.ModList;

import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

public class LangData {

	public static void addInfo(List<Component> list, Info... text) {
		if (Screen.hasShiftDown()) {
			boolean col = false;
			for (Info info : text) {
				list.add(info.get().withStyle(col ? Style.EMPTY.withColor(0x6abe30) : Style.EMPTY.withColor(0x5fcde4)));
				col = !col;
			}
		} else {
			list.add(Info.SHIFT.get().withStyle(ChatFormatting.GRAY));
			list.add(Info.PATCHOULI.get().withStyle(ModList.get().isLoaded("patchouli") ?
					ChatFormatting.GRAY : ChatFormatting.YELLOW));
		}
	}

	public enum IDS {
		BACKPACK_SLOT("tooltip.backpack_slot", 2, "Upgrade: %s/%s"),
		STORAGE_OWNER("tooltip.owner", 1, "Owner: %s"),
		BAG_SIZE("tooltip.item.size", 2, "Content: %s/%s"),
		DRAWER_CONTENT("tooltip.drawer.content", 2, "Content: %s x%s"),
		DUMP_FEEDBACK("chat.feedback.dump", 1, "Dumped %s items into target block"),
		LOAD_FEEDBACK("chat.feedback.load", 1, "Loaded %s items from target block"),
		EXTRACT_FEEDBACK("chat.feedback.extract", 1, "Extracted %s items"),
		COLLECT_FEEDBACK("chat.feedback.collect", 1, "Collected %s items"),
		NO_ITEM("chat.feedback.no_item", 0, "No item set for ender drawer. Cannot be placed."),
		LOOT("tooltip.info.loot", 0, "It may have loots inside"),
		MODE_NONE("tooltip.mode.none", 0, "No Pickup"),
		MODE_STACKING("tooltip.mode.stacking", 0, "Pickup to Stacking Slots Only"),
		MODE_ALL("tooltip.mode.all", 0, "Pickup all Fitting Items"),

		DESTROY_NONE("tooltip.destroy.none", 0, "No Destroy"),
		DESTROY_EXCESS("tooltip.destroy.excess", 0, "Destroy excess item"),
		DESTROY_MATCH("tooltip.destroy.matching", 0, "Destroy matching item"),
		DESTROY_ALL("tooltip.destroy.all", 0, "Destroy all items inserted");

		final String id, def;
		final int count;

		IDS(String id, int count, String def) {
			this.id = id;
			this.def = def;
			this.count = count;
		}

		public MutableComponent get(Object... objs) {
			if (objs.length != count)
				throw new IllegalArgumentException("for " + name() + ": expect " + count + " parameters, got " + objs.length);
			return Component.translatable(L2Backpack.MODID + "." + id, objs);
		}

	}

	public enum Info {
		SHIFT("tooltip.shift", "Press [shift] to show usage"),
		PATCHOULI("tooltip.patchouli", "Read Patchouli Book for details"),
		COLLECT_BAG("tooltip.collect.item", "Right click to store matching items in inventory, other than hotbar"),
		COLLECT_DRAWER("tooltip.collect.drawer", "Shift + right click to store matching items on inventory"),
		EXTRACT_BAG("tooltip.extract.item", "Shift + right click to throw out all stored items"),
		EXTRACT_DRAWER("tooltip.extract.drawer", "Right click to take one stack item out"),
		LOAD("tooltip.load", "Supports Load / Dump with Chest"),
		PLACE("tooltip.place", "Shift + right click to place it as a block"),
		KEYBIND("tooltip.keybind", "This can be put on chest slot (or back slot of Curios), and can be opened via key bind."),
		QUICK_INV_ACCESS("tooltip.info.quick_inv", "Right click to open. Or right click in inventory / ender chest / dimensional storage GUI to open directly."),
		QUICK_ANY_ACCESS("tooltip.info.quick_any", "Right click to open. Or right click in any GUI to open directly."),
		ARROW_INFO("tooltip.info.arrow_bag", "Put in off hand or chest slot (or back slot of Curios) and hold bow in main hand to preview, choose, and shoot arrows from quiver. Press up/down or [sneak] + number to switch arrows"),
		DRAWER_USE("tooltip.info.drawer", "In inventory, left click drawer with a stack to store item. Right click drawer to take item out. Drawer can only store 1 kind of simple item that has no NBT, but can store up to 64 stacks, or 512 stacks with full upgrades."),
		ENDER_DRAWER_USE("tooltip.info.ender_drawer_block", "For ender drawer block, right click it with item to store, and right click it with empty hand to retrieve a stack. For bulk transport, use drawer item to interact with it."),
		DIMENSIONAL("tooltip.info.dimensional", "All dimensional storage with the same color and owned by the same player shares the same inventory space, for both item and block form."),
		ENDER_DRAWER("tooltip.info.ender_drawer", "Same usage as drawer. All ender drawer set to the same item and owned by the same player shares the same inventory space, for both item and block form. Has same pickup option as regular drawer."),
		UPGRADE("tooltip.info.upgrade", "Upgrade by applying an Ender Pocket in Smithing Table. Content and name will be preserved."),
		EXIT("tooltip.info.exit", "When exiting GUI, it will return to the previous GUI if opened in accessible GUI. Press Shift + Esc to close all."),
		SCABBARD_INFO("tooltip.info.tool_bag", "Put in off hand or chest slot (or back slot of Curios). Sneak and hold tools or weapons in main hand (or hold nothing and press alt) to preview, choose, and swap tools from scabbard. Press up/down or [sneak] + number to switch tools. Press %s to swap", L2Keys.SWAP),
		ARMORBAG_INFO("tooltip.info.armor_bag", "Put in off hand or chest slot (or back slot of Curios). Sneak and hold nothing in main hand to preview, choose, and swap armors. Press up/down or [sneak] + number to switch armors. Press %s to swap", L2Keys.SWAP),
		SUIT_BAG_INFO("tooltip.info.suit_bag", "Same as Armor Swap but swaps full set at a time. It will exchange equipped items and selected items. Takes down player armor if the selected row has empty slot."),
		MULTI_SWITCH_INFO("tooltip.info.multi_switch", "This is a Quiver, a Tool Swap, and an Armor Swap at the same time. Sneak and hold respective items to trigger each mode. When holding nothing and pressing alt, tool swap mode is activated."),
		ENDER_SWITCH_INFO("tooltip.info.ender_switch", "This is a Combined Swap and an Ender Backpack at the same time. Note that the arrows, tools, and armors are stored within this item still, not in remote inventory. It inherits all properties of a backpack and an ender backpack."),
		INHERIT("tooltip.info.inherit", "Inherit all properties of a regular backpack, except that it cannot be upgraded. Can be placed in regular backpacks, but cannot open directly in regular backpack. Put it in dimensional storage for quick access."),
		PICKUP("tooltip.info.pickup", "Supports recursive pickup"),
		PICKUP_TWEAKER("tooltip.info.pickup_tweaker", "Right click backpacks or drawers in inventory with this item to switch pickup mode."),
		DESTROY_TWEAKER("tooltip.info.destroy_tweaker", "Right click backpacks or drawers in inventory with this item to switch destroy mode."),
		;

		private final String id, def;
		private final L2Keys[] key;


		Info(String id, String def, L2Keys... key) {
			this.id = id;
			this.def = def;
			this.key = key;
		}

		private MutableComponent get() {
			Object[] arr = new Object[key.length];
			for (int i = 0; i < key.length; i++) {
				arr[i] = Component.literal(L2Keys.SWAP.map.getKey().getName()).withStyle(ChatFormatting.YELLOW);
			}
			return Component.translatable(L2Backpack.MODID + "." + id, arr);
		}

	}

	public static void addTranslations(BiConsumer<String, String> pvd) {
		for (IDS id : IDS.values()) {
			pvd.accept(L2Backpack.MODID + "." + id.id, id.def);
		}
		for (Info id : Info.values()) {
			pvd.accept(L2Backpack.MODID + "." + id.id, id.def);
		}
		for (LootGen.HiddenPlayer pl : LootGen.HiddenPlayer.values()) {
			pvd.accept(L2Backpack.MODID + ".loot." + pl.id + ".name", pl.pname);
			pvd.accept(L2Backpack.MODID + ".loot." + pl.id + ".item", pl.bname);
		}
		pvd.accept(BackpackKeys.OPEN.id, "Open backpack on back");
	}

	public static String asId(String name) {
		return name.toLowerCase(Locale.ROOT);
	}

}
