package dev.xkmc.l2backpack.init.data;

import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

public class LangData {

	public static void addInfo(List<Component> list, Info... text) {
		if (Screen.hasShiftDown()) {
			boolean col = false;
			for (Info info : text) {
				list.add(info.get().withStyle(col ? ChatFormatting.YELLOW : ChatFormatting.GREEN));
				col = !col;
			}
		} else {
			list.add(Info.SHIFT.get().withStyle(ChatFormatting.GRAY));
		}
	}

	public enum IDS {
		BACKPACK_SLOT("tooltip.backpack_slot", 2, "Upgrade: %s/%s"),
		STORAGE_OWNER("tooltip.owner", 1, "Owner: %s"),
		BAG_SIZE("tooltip.bag.size", 2, "Content: %s/%s"),
		DRAWER_CONTENT("tooltip.drawer.content", 2, "Content: %s x%s"),
		DUMP_FEEDBACK("chat.feedback.dump", 1, "Dumped %s items into target block"),
		LOAD_FEEDBACK("chat.feedback.load", 1, "Loaded %s items from target block"),
		EXTRACT_FEEDBACK("chat.feedback.extract", 1, "Extracted %s items"),
		COLLECT_FEEDBACK("chat.feedback.collect", 1, "Collected %s items");

		final String id, def;
		final int count;

		IDS(String id, int count, String def) {
			this.id = id;
			this.def = def;
			this.count = count;
		}

		public Component get(Object... objs) {
			if (objs.length != count)
				throw new IllegalArgumentException("for " + name() + ": expect " + count + " parameters, got " + objs.length);
			return MutableComponent.create(new TranslatableContents(L2Backpack.MODID + "." + id, objs));
		}

	}

	public enum Info {
		SHIFT("tooltip.shift", "Press [shift] to show usage"),
		COLLECT_BAG("tooltip.collect.bag", "Right click to store matching items on inventory, other than hotbar"),
		COLLECT_DRAWER("tooltip.collect.drawer", "Shift + right click to store matching items on inventory"),
		EXTRACT_BAG("tooltip.extract.bag", "Shift + right click to throw out all stored items"),
		EXTRACT_DRAWER("tooltip.extract.drawer", "Right click to take one stack item out"),
		DUMP("tooltip.dump", "Shift + right click chests or other storage blocks to dump all items into the block"),
		LOAD("tooltip.load", "Shift + left click chests or other storage blocks to load items from the block"),
		PLACE("tooltip.place", "Shift + right click to place it as a block"),
		KEYBIND("tooltip.keybind", "This can be put on chest slot (or back slot of Curios), and can be opened via key bind."),
		QUICK_INV_ACCESS("tooltip.info.quick_inv", "Right click to open. Or right click in inventory / ender chest / dimensional storage GUI to open directly."),
		QUICK_ANY_ACCESS("tooltip.info.quick_any", "Right click to open. Or right click in any GUI to open directly."),
		ARROW_INFO("tooltip.info.arrow_bag", "Put in off hand or chest slot (or back slot of Curios) and hold bow in main hand to preview, choose, and shoot arrows from quiver. Press shift + number of up/down to switch arrows"),
		DRAWER_USE("tooltip.info.drawer", "In inventory, left click drawer with a stack to store item. Right click drawer to take item out. Drawer can only store 1 kind of simple item that has no NBT, but can store up to 64 stacks."),
		ENDER_DRAWER_USE("tooltip.info.ender_drawer", "For ender drawer block, right click it with item to store, and right click it with empty hand to retrieve a stack. For bulk transport, use drawer item to interact with it.");

		final String id, def;

		Info(String id, String def) {
			this.id = id;
			this.def = def;
		}

		private MutableComponent get() {
			return MutableComponent.create(new TranslatableContents(L2Backpack.MODID + "." + id));
		}

	}

	public static void addTranslations(BiConsumer<String, String> pvd) {
		for (IDS id : IDS.values()) {
			pvd.accept(L2Backpack.MODID + "." + id.id, id.def);
		}
		for (Info id : Info.values()) {
			pvd.accept(L2Backpack.MODID + "." + id.id, id.def);
		}
		pvd.accept("itemGroup.l2backpack.backpack", "L2 Backpack");
		pvd.accept("key.categories.l2backpack", "L2Backpack Keys");
		pvd.accept(Keys.OPEN.id, "Open backpack on back");
		pvd.accept(Keys.UP.id, "Arrow Select Up");
		pvd.accept(Keys.DOWN.id, "Arrow Select Down");
	}

	public static String asId(String name) {
		return name.toLowerCase(Locale.ROOT);
	}

}
