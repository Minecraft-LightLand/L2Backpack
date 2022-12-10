package dev.xkmc.l2backpack.init.data;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class BackpackConfig {

	public static class Client {

		public final ForgeConfigSpec.BooleanValue previewOnCenter;

		Client(ForgeConfigSpec.Builder builder) {
			previewOnCenter = builder.comment("Put quiver preview near the center of the screen, rather than edge of the screen")
					.define("previewOnCenter", true);
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

	/**
	 * Registers any relevant listeners for config
	 */
	public static void init() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BackpackConfig.CLIENT_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BackpackConfig.COMMON_SPEC);
	}


}
