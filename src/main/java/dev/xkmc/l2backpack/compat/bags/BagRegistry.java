package dev.xkmc.l2backpack.compat.bags;

import com.tterrag.registrate.providers.ProviderType;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.TagGen;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraftforge.fml.ModList;

public class BagRegistry {

	public static void register() {
		if (ModList.get().isLoaded(Apotheosis.MODID)) {
			var entry = BackpackItems.regBag("gem_bag", p -> new CompatBag(p, ApothProxy::pred))
					.recipe(ApothProxy::recipe)
					.defaultLang().register();
			L2Backpack.REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, pvd ->
					pvd.addTag(TagGen.BAGS).addOptional(entry.getId()));
		}
		if (ModList.get().isLoaded(IronsSpellbooks.MODID)) {
			var entry = BackpackItems.regBag("scroll_bag", p -> new CompatBag(p, ISSProxy::pred))
					.recipe(ISSProxy::recipe)
					.defaultLang().register();
			L2Backpack.REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, pvd ->
					pvd.addTag(TagGen.BAGS).addOptional(entry.getId()));
		}
	}

}
