package dev.xkmc.l2backpack.content.drawer;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Optional;

public class DrawerItem extends BaseDrawerItem {

	private static final String COUNT = "drawerCount";
	private static final int MAX = 1;//TODO

	public static int getCount(ItemStack drawer) {
		return Optional.ofNullable(drawer.getTag()).map(e -> e.getInt(COUNT)).orElse(0);
	}

	public static void setCount(ItemStack drawer, int count) {
		drawer.getOrCreateTag().putInt(COUNT, count);
	}

	public DrawerItem(Properties properties) {
		super(properties);
	}

	@Override
	public void insert(ItemStack drawer, ItemStack stack) {
		int count = getCount(drawer);
		int allow = MAX * stack.getMaxStackSize() - count;
		setCount(drawer, count + Math.min(allow, stack.getCount()));
		stack.shrink(allow);
	}

	@Override
	public ItemStack takeItem(ItemStack drawer) {
		Item item = getItem(drawer);
		if (item == Items.AIR) return ItemStack.EMPTY;
		int count = getCount(drawer);
		int take = Math.min(count, item.getMaxStackSize());
		setCount(drawer, count - take);
		return new ItemStack(item, take);
	}

	@Override
	public boolean canSetNewItem(ItemStack drawer) {
		return getItem(drawer) == Items.AIR || getCount(drawer) == 0;
	}

}
