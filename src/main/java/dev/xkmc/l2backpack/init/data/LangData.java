package dev.xkmc.l2backpack.init.data;

import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Locale;
import java.util.function.BiConsumer;

public class LangData {

	public enum IDS {
		BACKPACK_SLOT("tooltip.backpack_slot", 2, "Upgrade: %s/%s"),
		STORAGE_OWNER("tooltip.owner", 1, "Owner: %s"),
		BAG_SIZE("tooltip.bag.size", 2, "Content: %s/%s"),
		BAG_INFO("tooltip.bag.info", 0, "Right click to store, shift right click to dump");

		final String id, def;
		final int count;

		IDS(String id, int count, String def) {
			this.id = id;
			this.def = def;
			this.count = count;
		}

		public TranslatableComponent get(Object... objs) {
			if (objs.length != count)
				throw new IllegalArgumentException("for " + name() + ": expect " + count + " parameters, got " + objs.length);
			return new TranslatableComponent(L2Backpack.MODID + "." + id, objs);
		}

	}

	public static void addTranslations(BiConsumer<String, String> pvd) {
		for (IDS id : IDS.values()) {
			String[] strs = id.id.split("\\.");
			String str = strs[strs.length - 1];
			pvd.accept(L2Backpack.MODID + "." + id.id, id.def);
		}
		pvd.accept("itemGroup.l2backpack.backpack", "L2 Backpack");
	}

	public static String asId(String name) {
		return name.toLowerCase(Locale.ROOT);
	}

}
