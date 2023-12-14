package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import net.minecraft.world.item.ItemStack;

public abstract class SetSwapItem extends BaseBagItem implements ISetSwapItem {

	private final int rows;

	public SetSwapItem(Properties props, int rows) {
		super(props.stacksTo(1).fireResistant());
		this.rows = rows;
	}

	@Override
	public int getRows(ItemStack stack) {
		return rows;
	}

	@Override
	public int getRows() {
		return rows;
	}

}
