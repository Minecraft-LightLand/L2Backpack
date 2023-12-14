package dev.xkmc.l2backpack.content.quickswap.armorswap;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.content.common.BagSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.inventory.InventoryMenu.*;

public class ArmorSetBagSlot extends BagSlot {

	private static final ResourceLocation[] TEXTURE_EMPTY_SLOTS = new ResourceLocation[]{
			EMPTY_ARMOR_SLOT_HELMET, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_BOOTS};
	private final int index;

	public ArmorSetBagSlot(IItemHandlerModifiable handler, int index, int x, int y) {
		super(handler, index, x, y);
		this.index = index;
	}

	@Nullable
	@Override
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
		return Pair.of(BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS[index / 9]);
	}

}
