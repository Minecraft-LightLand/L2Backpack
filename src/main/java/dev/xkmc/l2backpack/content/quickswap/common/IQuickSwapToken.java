package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.quickswap.entry.ISwapEntry;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import dev.xkmc.l2itemselector.wheel.WheelAdaptor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public interface IQuickSwapToken<T extends ISwapEntry<T>> {

	void setSelected(int slot);

	List<T> getList();

	int getSelected();

	void shrink(int i);

	QuickSwapType type();

	void swap(Player player);

	ItemStack stack();

	Optional<WheelAdaptor<?>> get(@Nullable Player player, int wheelIndex);

}
