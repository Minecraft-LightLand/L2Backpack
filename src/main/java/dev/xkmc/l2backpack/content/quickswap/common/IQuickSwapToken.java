package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.quickswap.entry.ISwapEntry;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IQuickSwapToken<T extends ISwapEntry<T>> {

	ItemStack stack();

	void setSelected(int slot);

	List<T> getList();

	int getSelected();

	void shrink(int i);

	QuickSwapType type();

	void swap(Player player);

	boolean isLocked(int i);

}
