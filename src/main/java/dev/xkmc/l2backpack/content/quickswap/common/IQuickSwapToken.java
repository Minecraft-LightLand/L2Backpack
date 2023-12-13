package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.quickswap.type.OverlayToken;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface IQuickSwapToken<T extends OverlayToken<T>> {

	void setSelected(int slot);

	List<T> getList();

	int getSelected();

	void shrink(int i);

	QuickSwapType type();

	void swap(Player player);

}
