package dev.xkmc.l2backpack.init.data;

import dev.xkmc.l2library.repack.registrate.providers.RegistrateLangProvider;
import dev.xkmc.l2backpack.init.LightLand;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Locale;
import java.util.function.BiConsumer;

public class LangData {

	public enum IDS {
		BACKPACK_SLOT("tooltip.backpack_slot", 2),
		STORAGE_OWNER("tooltip.owner", 1);

		final String id;
		final int count;

		IDS(String id, int count) {
			this.id = id;
			this.count = count;
		}

		public TranslatableComponent get(Object... objs) {
			if (objs.length != count)
				throw new IllegalArgumentException("for " + name() + ": expect " + count + " parameters, got " + objs.length);
			return new TranslatableComponent(LightLand.MODID + "." + id, objs);
		}

	}

	public static void addTranslations(BiConsumer<String, String> pvd) {
		for (IDS id : IDS.values()) {
			String[] strs = id.id.split("\\.");
			String str = strs[strs.length - 1];
			pvd.accept(LightLand.MODID + "." + id.id,
					RegistrateLangProvider.toEnglishName(str) + getParams(id.count));
		}
		pvd.accept("itemGroup.l2backpack.backpack", "L2 Backpack");
	}

	private static String getParams(int count) {
		if (count <= 0)
			return "";
		StringBuilder pad = new StringBuilder();
		for (int i = 0; i < count; i++) {
			pad.append(pad.length() == 0 ? ": " : "/");
			pad.append("%s");
		}
		return pad.toString();
	}

	public static String asId(String name) {
		return name.toLowerCase(Locale.ROOT);
	}

}
