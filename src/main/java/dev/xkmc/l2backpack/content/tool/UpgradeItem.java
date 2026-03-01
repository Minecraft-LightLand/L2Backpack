package dev.xkmc.l2backpack.content.tool;

import dev.xkmc.l2backpack.content.backpack.BackpackItem;
import dev.xkmc.l2backpack.content.bag.AbstractBag;
import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.drawer.DrawerBlockEntity;
import dev.xkmc.l2backpack.content.drawer.DrawerItem;
import dev.xkmc.l2backpack.init.data.LBLang;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class UpgradeItem extends Item implements IBagTool {

	public UpgradeItem(Properties properties) {
		super(properties);
	}

	@Override
	public void click(ItemStack tool, ItemStack stack) {
		if (stack.getItem() instanceof BackpackItem bag) {
			int row = bag.getRows(stack);
			if (row < BackpackItem.MAX_ROW) {
				stack.set(LBItems.DC_ROW, row + 1);
				tool.shrink(1);
			}
		}
		if (stack.getItem() instanceof DrawerItem) {
			int size = stack.getOrDefault(LBItems.DC_DRAWER_STACKING, 1);
			if (size < BaseDrawerItem.MAX_FACTOR) {
				stack.set(LBItems.DC_DRAWER_STACKING, size + 1);
				tool.shrink(1);
			}
		}

		if (stack.getItem() instanceof AbstractBag bag) {
			int size = AbstractBag.getSizeFactor(stack);
			if (size < AbstractBag.MAX_FACTOR) {
				AbstractBag.setSizeFactor(stack, size+1);
				tool.shrink(1);
			}
		}
	}

	@Override
	public ItemInteractionResult clickDrawerBlock(ItemStack stack, DrawerBlockEntity chest) {
		if (chest.handler.stacking < BaseDrawerItem.MAX_FACTOR) {
			if (chest.getLevel() == null || chest.getLevel().isClientSide)
				return ItemInteractionResult.SUCCESS;
			chest.handler.stacking++;
			chest.sync();
			stack.shrink(1);
			return ItemInteractionResult.SUCCESS;
		}
		return IBagTool.super.clickDrawerBlock(stack, chest);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
		list.add(LBLang.IDS.UPGRADE.get().withStyle(ChatFormatting.GRAY));
	}
}
