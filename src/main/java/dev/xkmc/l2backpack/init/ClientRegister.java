package dev.xkmc.l2backpack.init;

import dev.xkmc.l2library.repack.registrate.util.entry.ItemEntry;
import dev.xkmc.l2backpack.content.item.BackpackItem;
import dev.xkmc.l2backpack.content.item.EnderBackpackItem;
import dev.xkmc.l2backpack.init.registrate.LightlandItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientRegister {

	@OnlyIn(Dist.CLIENT)
	public static void registerItemProperties() {
		for (ItemEntry<BackpackItem> entry : LightlandItems.BACKPACKS) {
			ItemProperties.register(entry.get(), new ResourceLocation("open"), BackpackItem::isOpened);
		}
		ItemProperties.register(LightlandItems.ENDER_BACKPACK.get(), new ResourceLocation("open"), EnderBackpackItem::isOpened);
	}

}
