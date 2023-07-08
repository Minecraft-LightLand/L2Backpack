package dev.xkmc.l2backpack.content.common;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record InvTooltip(BaseBagItem item, ItemStack stack) implements TooltipComponent {

	public static Optional<TooltipComponent> get(BaseBagItem item, ItemStack stack) {
		if (Screen.hasShiftDown()) {
			return Optional.empty();
		}
		if (BaseBagItem.getListTag(stack).size() > 0) {
			return Optional.of(new InvTooltip(item, stack));
		}
		return Optional.empty();
	}

}
