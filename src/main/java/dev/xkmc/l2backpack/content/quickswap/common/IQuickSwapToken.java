package dev.xkmc.l2backpack.content.quickswap.common;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IQuickSwapToken {

	void setSelected(int slot);

	List<ItemStack> getList();

	boolean isTokenValid(LocalPlayer player);

	int getSelected();

	void shrink(int i);

	QuickSwapType type();

}
