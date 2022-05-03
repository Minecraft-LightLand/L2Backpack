package dev.xkmc.l2backpack.init.data;

import dev.xkmc.l2backpack.init.LightLand;
import dev.xkmc.l2library.repack.registrate.providers.ProviderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static dev.xkmc.l2backpack.init.LightLand.REGISTRATE;

public enum ItemTags {
	BACKPACKS,
	DIMENSIONAL_STORAGES;

	public final TagKey<Item> tag;

	ItemTags() {
		ResourceLocation id = new ResourceLocation(LightLand.MODID, LangData.asId(name()));
		tag = net.minecraft.tags.ItemTags.create(id);
		REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> prov.tag(tag));
	}

	public boolean matches(ItemStack stack) {
		return stack.is(tag);
	}

	public void add(Item... values) {
		REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> prov.tag(tag)
				.add(values));
	}

}
