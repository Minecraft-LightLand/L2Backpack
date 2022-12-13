package dev.xkmc.l2backpack.content.quickswap.merged;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapItem;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record MultiSwapToken(IQuickSwapItem item, ItemStack stack,
							 QuickSwapType type) implements IQuickSwapToken {

	public void setSelected(int slot) {
		MultiSwitch.setSelected(stack, type, slot);
	}

	public List<ItemStack> getList() {
		return BaseBagItem.getItems(stack).subList(type.ordinal() * 9, type.ordinal() * 9 + 9);
	}

	public int getSelected() {
		return MultiSwitch.getSelected(stack, type);
	}

	public void shrink(int i) {
		List<ItemStack> list = BaseBagItem.getItems(stack);
		List<ItemStack> sublist = list.subList(type.ordinal() * 9, type.ordinal() * 9 + 9);
		sublist.get(getSelected()).shrink(i);
		BaseBagItem.setItems(stack, list);
	}

	@Override
	public void swap(Player player) {
		if (type == QuickSwapType.ARROW)
			return;
		List<ItemStack> list = BaseBagItem.getItems(stack);
		List<ItemStack> sublist = list.subList(type.ordinal() * 9, type.ordinal() * 9 + 9);
		int i = getSelected();
		ItemStack a = sublist.get(i);
		if (type == QuickSwapType.TOOL) {
			sublist.set(i, player.getMainHandItem());
			player.setItemInHand(InteractionHand.MAIN_HAND, a);
		}
		if (type == QuickSwapType.ARMOR) {
			if (a.isEmpty()) return;
			EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(a);
			sublist.set(i, player.getItemBySlot(slot));
			player.setItemSlot(slot, a);
		}
		BaseBagItem.setItems(stack, list);
	}

}
