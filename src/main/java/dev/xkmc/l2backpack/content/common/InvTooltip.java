package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.content.remote.player.EnderBackpackItem;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record InvTooltip(TooltipInvItem item, ItemStack stack) implements TooltipComponent {

	public static Optional<TooltipComponent> get(BaseBagItem item, ItemStack stack) {
		if (Screen.hasShiftDown()) {
			return Optional.empty();
		}
		var list = BaseBagItem.getListTag(stack);
		if (list.size() > 0) {
			return Optional.of(new InvTooltip(item, stack));
		}
		return Optional.empty();
	}

	public static Optional<TooltipComponent> get(EnderBackpackItem item, ItemStack stack) {
		if (Screen.hasShiftDown()) {
			return Optional.empty();
		}
		return Optional.of(new InvTooltip(item, stack));
	}

}
