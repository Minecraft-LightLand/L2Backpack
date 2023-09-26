package dev.xkmc.l2backpack.init.data;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class BackpackConfig {

	public static class Client {

		public final ForgeConfigSpec.BooleanValue previewOnCenter;

		public final ForgeConfigSpec.BooleanValue showArrowOnlyWithShift;
		public final ForgeConfigSpec.BooleanValue showToolOnlyWithShift;
		public final ForgeConfigSpec.BooleanValue showArmorOnlyWithShift;

		public final ForgeConfigSpec.BooleanValue reverseScroll;


		Client(ForgeConfigSpec.Builder builder) {
			previewOnCenter = builder.comment("Put quiver preview near the center of the screen, rather than edge of the screen")
					.define("previewOnCenter", true);

			showArrowOnlyWithShift = builder.comment("Show arrow quick swap only when shift is pressed")
					.define("showArrowOnlyWithShift", false);

			showToolOnlyWithShift = builder.comment("Show arrow quick swap only when shift is pressed")
					.define("showToolOnlyWithShift", true);

			showArmorOnlyWithShift = builder.comment("Show arrow quick swap only when shift is pressed")
					.define("showArmorOnlyWithShift", true);

			reverseScroll = builder.comment("Reverse scrolling direction for quick swap")
					.define("reverseScroll", false);

		}


	}

	public static class Common {

		public final ForgeConfigSpec.IntValue initialRows;

		Common(ForgeConfigSpec.Builder builder) {
			initialRows = builder.comment("Initial Rows (x9 slots) for backpack")
					.defineInRange("initialRows", 2, 1, 6);
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
