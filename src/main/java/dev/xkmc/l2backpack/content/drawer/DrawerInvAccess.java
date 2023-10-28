package dev.xkmc.l2backpack.content.drawer;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record DrawerInvAccess(ItemStack drawerStack, DrawerItem drawerItem) implements BaseDrawerInvAccess {

	@SuppressWarnings("ConstantConditions")
	@Override
	public ServerPlayer player() {
		return null;
	}

	@Override
	public int getStoredCount() {
		return DrawerItem.getCount(drawerStack());
	}

	@Override
	public boolean isEmpty() {
		return drawerItem().canSetNewItem(drawerStack());
	}

	@Override
	public void setStoredCount(int count) {
		DrawerItem.setCount(drawerStack(), count);
	}

}
