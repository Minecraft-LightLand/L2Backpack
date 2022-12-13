package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record SingleSwapToken(IQuickSwapItem item, ItemStack stack,
							  QuickSwapType type) implements IQuickSwapToken {

	public void setSelected(int slot) {
		SingleSwapItem.setSelected(stack, slot);
	}

	public List<ItemStack> getList() {
		return BaseBagItem.getItems(stack);
	}

	public int getSelected() {
		return SingleSwapItem.getSelected(stack);
	}

	public void shrink(int i) {
		List<ItemStack> list = getList();
		list.get(getSelected()).shrink(i);
		BaseBagItem.setItems(stack, list);
	}

	@Override
	public void swap(Player player) {
		if (type == QuickSwapType.ARROW)
			return;
		List<ItemStack> list = getList();
		int i = getSelected();
		ItemStack a = list.get(i);
		if (type == QuickSwapType.TOOL) {
			list.set(i, player.getMainHandItem());
			player.setItemInHand(InteractionHand.MAIN_HAND, a);
		}
		if (type == QuickSwapType.ARMOR) {
			EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(a);
			list.set(i, player.getItemBySlot(slot));
			player.setItemSlot(slot, a);
		}
		BaseBagItem.setItems(stack, list);
	}

}
