package dev.xkmc.l2backpack.init.data;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;

import static dev.xkmc.l2backpack.content.backpack.BackpackItem.MAX_ROW;

public class BackpackConfig {

	public static class Client {

		public final ForgeConfigSpec.BooleanValue previewOnCenter;

		public final ForgeConfigSpec.BooleanValue popupArrowOnSwitch;
		public final ForgeConfigSpec.BooleanValue popupToolOnSwitch;
		public final ForgeConfigSpec.BooleanValue popupArmorOnSwitch;

		public final ForgeConfigSpec.BooleanValue reverseScroll;
		public final ForgeConfigSpec.BooleanValue backpackInsertRequiresShift;
		public final ForgeConfigSpec.BooleanValue backpackEnableLeftClickInsert;
		public final ForgeConfigSpec.BooleanValue backpackEnableRightClickInsert;


		Client(ForgeConfigSpec.Builder builder) {
			previewOnCenter = builder.comment("Put quiver preview near the center of the screen, rather than edge of the screen")
					.define("previewOnCenter", true);

			popupArrowOnSwitch = builder.comment("Show arrow quick swap when switching to a bow")
					.define("showArrowOnlyWithShift", true);

			popupToolOnSwitch = builder.comment("Show tool quick swap when switching to a tool")
					.define("showToolOnlyWithShift", false);

			popupArmorOnSwitch = builder.comment("Show armor quick swap when switching to empty hand")
					.define("showArmorOnlyWithShift", false);

			reverseScroll = builder.comment("Reverse scrolling direction for quick swap")
					.define("reverseScroll", false);

			backpackInsertRequiresShift = builder.comment("Backpack inventory quick insert requires shift click")
					.define("backpackInsertRequiresShift", false);

			backpackEnableLeftClickInsert = builder.comment("Backpack inventory quick insert allows left click insert")
					.define("backpackEnableLeftClickInsert", true);

			backpackEnableRightClickInsert = builder.comment("Backpack inventory quick insert allows right click insert")
					.define("backpackEnableRightClickInsert", true);

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

	public static class Common {

		public final ForgeConfigSpec.IntValue initialRows;

		Common(ForgeConfigSpec.Builder builder) {
			initialRows = builder.comment("Initial Rows (x9 slots) for backpack")
					.defineInRange("initialRows", 2, 1, MAX_ROW);
		}
	}

	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final Client CLIENT;

	public static final ForgeConfigSpec COMMON_SPEC;
	public static final Common COMMON;

	static {
		final Pair<Client, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = client.getRight();
		CLIENT = client.getLeft();

		final Pair<Common, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = common.getRight();
		COMMON = common.getLeft();
	}

	public static void init() {
		register(ModConfig.Type.CLIENT, CLIENT_SPEC);
		register(ModConfig.Type.COMMON, COMMON_SPEC);
	}

	private static void register(ModConfig.Type type, IConfigSpec<?> spec) {
		var mod = ModLoadingContext.get().getActiveContainer();
		String path = "l2_configs/" + mod.getModId() + "-" + type.extension() + ".toml";
		ModLoadingContext.get().registerConfig(type, spec, path);
	}

}
