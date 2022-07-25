package dev.xkmc.l2backpack.init.data;

import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.Locale;
import java.util.function.BiConsumer;

public class LangData {

	public enum IDS {
		BACKPACK_SLOT("tooltip.backpack_slot", 2, "Upgrade: %s/%s"),
		STORAGE_OWNER("tooltip.owner", 1, "Owner: %s"),
		BAG_SIZE("tooltip.bag.size", 2, "Content: %s/%s"),
		BAG_INFO("tooltip.bag.info", 0, "Right click to store, shift right click to dump"),
		ARROW_INFO("tooltip.arrow_bag", 0, "Put in off hand (or Curios slot), hold bow in main hand, press shift + number of up/down to switch arrows");

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
