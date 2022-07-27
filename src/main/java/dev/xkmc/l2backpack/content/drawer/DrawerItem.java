package dev.xkmc.l2backpack.content.drawer;

import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class DrawerItem extends BaseDrawerItem {

	private static final String COUNT = "drawerCount";
	private static final int MAX = 64;

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

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		Item item = getItem(stack);
		int count = getCount(stack);
		if (item != Items.AIR && count > 0) {
			list.add(LangData.IDS.DRAWER_CONTENT.get(item.getDescription(), count));
		}
		list.add(LangData.IDS.DRAWER_INFO.get());
	}
}
