package dev.xkmc.l2backpack.compat.bags;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.xkmc.l2backpack.init.data.LBTagGen;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.neoforged.fml.ModList;

public class BagRegistry {

	public static void register() {
		if (ModList.get().isLoaded(Apotheosis.MODID)) {
			LBItems.regBag("gem_bag", p -> new CompatBag(p, ApothProxy::pred))
					.asOptional().tag(LBTagGen.BAGS)
					.recipe(ApothProxy::recipe)
					.defaultLang().register();
		}
		if (ModList.get().isLoaded(IronsSpellbooks.MODID)) {
			LBItems.regBag("scroll_bag", p -> new CompatBag(p, ISSProxy::pred))
					.asOptional().tag(LBTagGen.BAGS)
					.recipe(ISSProxy::recipe)
					.defaultLang().register();
		}
	}

}
