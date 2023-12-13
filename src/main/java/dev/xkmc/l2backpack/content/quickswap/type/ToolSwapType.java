package dev.xkmc.l2backpack.content.quickswap.type;

import dev.xkmc.l2backpack.init.data.BackpackConfig;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class ToolSwapType extends QuickSwapType {

	public ToolSwapType(String name, int index) {
		super(name, index);
	}

	@Override
	public boolean activePopup() {
		return BackpackConfig.CLIENT.popupToolOnSwitch.get();
	}

	@Override
	public ItemStack getSignatureItem(Player player) {
		return player.getMainHandItem();
	}

	@Override
	public boolean canSwap() {
		return true;
	}

	@Override
	public void swap(Player player, ItemStack stack, Consumer<ItemStack> cons) {
		cons.accept(player.getMainHandItem());
		player.setItemInHand(InteractionHand.MAIN_HAND, stack);
	}

}
