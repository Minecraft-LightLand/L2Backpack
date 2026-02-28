package dev.xkmc.l2backpack.content.tool;

import dev.xkmc.l2backpack.content.drawer.DrawerBlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public interface IBagTool {

	void click(ItemStack tool, ItemStack stack);

	default InteractionResult clickDrawerBlock(ItemStack stack, DrawerBlockEntity chest) {
		return InteractionResult.PASS;
	}

}
