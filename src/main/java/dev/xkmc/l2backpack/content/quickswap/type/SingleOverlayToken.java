package dev.xkmc.l2backpack.content.quickswap.type;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record SingleOverlayToken(ItemStack stack) implements OverlayToken<SingleOverlayToken> {

	public static List<SingleOverlayToken> parse(List<ItemStack> list) {
		return list.stream().map(SingleOverlayToken::new).toList();
	}

	@Override
	public List<ItemStack> asList() {
		return List.of(stack);
	}

	@Override
	public ItemStack getStack() {
		return stack;
	}

}
