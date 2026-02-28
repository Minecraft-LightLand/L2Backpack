package dev.xkmc.l2backpack.content.tool;

import dev.xkmc.l2backpack.content.drawer.DrawerBlockEntity;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;

public interface IBagTool {

	void click(ItemStack tool, ItemStack stack);

	default ItemInteractionResult clickDrawerBlock(ItemStack stack, DrawerBlockEntity chest) {
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}
}
