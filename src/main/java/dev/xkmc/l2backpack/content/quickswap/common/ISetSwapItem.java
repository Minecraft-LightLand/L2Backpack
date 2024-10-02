package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.quickswap.set.ISetToggle;
import dev.xkmc.l2core.base.menu.data.BoolArrayDataSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ISetSwapItem extends IQuickSwapItem {

	int getRows();

	ISetToggle getToggle(ItemStack stack, @Nullable BoolArrayDataSlot dataSlot);

}
