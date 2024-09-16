package dev.xkmc.l2backpack.init.data;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2core.util.ConfigInit;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.lwjgl.glfw.GLFW;

import static dev.xkmc.l2backpack.content.backpack.BackpackItem.MAX_ROW;

public class LBConfig {

	public static class Client extends ConfigInit {

		public final ModConfigSpec.BooleanValue previewOnCenter;

		public final ModConfigSpec.BooleanValue popupArrowOnSwitch;
		public final ModConfigSpec.BooleanValue popupToolOnSwitch;
		public final ModConfigSpec.BooleanValue popupArmorOnSwitch;

		public final ModConfigSpec.BooleanValue drawerAlwaysRenderFlat;

		public final ModConfigSpec.BooleanValue reverseScroll;
		public final ModConfigSpec.BooleanValue backpackInsertRequiresShift;
		public final ModConfigSpec.BooleanValue backpackEnableLeftClickInsert;
		public final ModConfigSpec.BooleanValue backpackEnableRightClickInsert;


		Client(Builder builder) {
			markL2();
			previewOnCenter = builder
					.text("Arrow Display on Center")
					.comment("Put quiver preview near the center of the screen, rather than edge of the screen")
					.define("previewOnCenter", true);

			popupArrowOnSwitch = builder
					.text("Popup quiver on bow switch")
					.comment("Show arrow quick swap when switching to a bow")
					.define("popupArrowOnSwitch", true);

			popupToolOnSwitch = builder
					.text("Popup tool swap on switch")
					.comment("Show tool quick swap when switching to a tool")
					.define("popupToolOnSwitch", false);

			popupArmorOnSwitch = builder
					.text("Popup armor swap on switch")
					.comment("Show armor quick swap when switching to empty hand")
					.define("popupArmorOnSwitch", false);

			reverseScroll = builder
					.text("Reverse scroll")
					.comment("Reverse scrolling direction for quick swap")
					.define("reverseScroll", false);

			backpackInsertRequiresShift = builder
					.text("Require SHIFT to insert")
					.comment("Backpack inventory quick insert requires shift click")
					.define("backpackInsertRequiresShift", false);

			backpackEnableLeftClickInsert = builder
					.text("Allow backpack left click insert")
					.comment("Backpack inventory quick insert allows left click insert")
					.define("backpackEnableLeftClickInsert", true);

			backpackEnableRightClickInsert = builder
					.text("Allow backpack right click insert")
					.comment("Backpack inventory quick insert allows right click insert")
					.define("backpackEnableRightClickInsert", true);

			drawerAlwaysRenderFlat = builder
					.text("Draws Always render content directly")
					.define("drawerAlwaysRenderFlat", false);

		}

		public boolean allowBackpackInsert(int button) {
			if (backpackInsertRequiresShift.get()) {
				if (!Screen.hasShiftDown())
					return false;
			}
			boolean allow = false;
			if (backpackEnableLeftClickInsert.get()) {
				if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
					allow = true;
			}
			if (backpackEnableRightClickInsert.get()) {
				if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT)
					allow = true;
			}
			return allow;
		}

	}

	public static class Server extends ConfigInit {

		public final ModConfigSpec.IntValue initialRows;

		public final ModConfigSpec.IntValue startupBackpackCondition;

		public final ModConfigSpec.BooleanValue sophisticatedEnderTicking;
		public final ModConfigSpec.BooleanValue sophisticatedRightClickOpen;

		Server(Builder builder) {
			markL2();
			initialRows = builder.text("Initial Rows for backpack")
					.defineInRange("initialRows", 2, 1, MAX_ROW);
			startupBackpackCondition = builder
					.text("Startup packing count")
					.comment("How many items do players need to spawn with to have the privilege of having them in a backpack")
					.defineInRange("startupBackpackCondition", 6, 1, 36);
			sophisticatedEnderTicking = builder.text("Tick Sophisticated backpacks in ender backpack")
					.define("sophisticatedEnderTicking", true);
			sophisticatedRightClickOpen = builder.text("Right click Sophisticated backpacks to open in inventory and ender backpack")
					.define("sophisticatedRightClickOpen", true);

		}
	}

	public static final Client CLIENT = L2Backpack.REGISTRATE.registerClient(Client::new);
	public static final Server SERVER = L2Backpack.REGISTRATE.registerSynced(Server::new);

	public static void init() {
	}

}
