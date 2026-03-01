package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.content.bag.AbstractBag;
import dev.xkmc.l2backpack.content.remote.player.EnderBackpackItem;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record InvTooltip(TooltipInvItem item, ItemStack stack, int w, int h) implements TooltipComponent {

	public InvTooltip(TooltipInvItem item, ItemStack stack) {
		this(item, stack, item.getRowSize(), item.getInvSize(stack) / item.getRowSize());
	}

	public static Optional<TooltipComponent> get(BaseBagItem item, ItemStack stack) {
		if (Screen.hasShiftDown()) {
			return Optional.empty();
		}
		var list = BaseBagItem.getListTag(stack);
		if (!list.isEmpty()) {
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


	public static Optional<TooltipComponent> get(AbstractBag item, ItemStack stack) {
		if (Screen.hasAltDown()) {
			int count = item.getLastIndex(stack);
			int w = Math.max(item.getRowSize(), (int) Math.ceil(Math.sqrt(count)));
			int h = count / w + 1;
			return Optional.of(new InvTooltip(item, stack, w, h));
		}
		return Optional.empty();
	}

}
