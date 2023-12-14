package dev.xkmc.l2backpack.content.quickswap.type;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public interface ISwapAction {

	void swap(Player player, ItemStack stack, Consumer<ItemStack> list);

}
