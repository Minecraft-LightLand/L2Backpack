package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.quiver.ArrowBag;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import java.util.List;

public record SingleSwapToken(IQuickSwapItem item, ItemStack stack,
							  QuickSwapType type) implements IQuickSwapToken {

	public void setSelected(int slot) {
		ArrowBag.setSelected(stack, slot);
	}

	public List<ItemStack> getList() {
		return BaseBagItem.getItems(stack);
	}

	public boolean isTokenValid(LocalPlayer player) {
		if (!(player.getMainHandItem().getItem() instanceof ProjectileWeaponItem weapon)) return false;
		for (ItemStack stack : getList()) {
			if (!stack.isEmpty() && weapon.getAllSupportedProjectiles().test(stack)) return true;
		}
		return true;
	}

	public int getSelected() {
		return ArrowBag.getSelected(stack);
	}

	public void shrink(int i) {
		List<ItemStack> list = getList();
		list.get(getSelected()).shrink(i);
		BaseBagItem.setItems(stack, list);
	}

}
