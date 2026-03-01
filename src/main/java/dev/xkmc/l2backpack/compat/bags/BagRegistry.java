package dev.xkmc.l2backpack.compat.bags;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraftforge.fml.ModList;

public class BagRegistry {

	public static void register() {
		if (ModList.get().isLoaded(Apotheosis.MODID)) {
			BackpackItems.regBag("gem_bag", p -> new CompatBag(p, ApothProxy::pred))
					.recipe(ApothProxy::recipe)
					.defaultLang().register();
		}
		if (ModList.get().isLoaded(IronsSpellbooks.MODID)) {
			BackpackItems.regBag("scroll_bag", p -> new CompatBag(p, ISSProxy::pred))
					.recipe(ISSProxy::recipe)
					.defaultLang().register();
		}
	}

}
