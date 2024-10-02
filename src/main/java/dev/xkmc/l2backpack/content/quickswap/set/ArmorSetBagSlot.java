package dev.xkmc.l2backpack.content.quickswap.set;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.inventory.InventoryMenu.*;

public class ArmorSetBagSlot extends GenericSetBagSlot {

	private static final ResourceLocation[] TEXTURE_EMPTY_SLOTS = new ResourceLocation[]{
			EMPTY_ARMOR_SLOT_HELMET, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_BOOTS
	};

	public ArmorSetBagSlot(IItemHandlerModifiable handler, ISetToggle toggle, int index, int x, int y) {
		super(handler, toggle, index, x, y);
	}

	@Nullable
	@Override
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
		return isDisabled() ? super.getNoItemIcon() : Pair.of(BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS[index / 9]);
	}

}
