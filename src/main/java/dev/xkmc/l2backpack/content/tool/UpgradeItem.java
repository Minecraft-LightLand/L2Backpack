package dev.xkmc.l2backpack.content.tool;

import dev.xkmc.l2backpack.content.backpack.BackpackItem;
import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.drawer.DrawerBlockEntity;
import dev.xkmc.l2backpack.content.drawer.DrawerItem;
import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

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
				BackpackItem.setRow(stack, row + 1);
				tool.shrink(1);
			}
		}
		if (stack.getItem() instanceof DrawerItem) {
			int size = BaseDrawerItem.getStackingFactor(stack);
			if (size < BaseDrawerItem.MAX_FACTOR) {
				BaseDrawerItem.setStackingFactor(stack, size + 1);
				tool.shrink(1);
			}
		}
	}

	@Override
	public InteractionResult clickDrawerBlock(ItemStack stack, DrawerBlockEntity chest) {
		int size = BaseDrawerItem.getStackingFactor(chest.handler.config);
		if (size < BaseDrawerItem.MAX_FACTOR) {
			if (chest.getLevel() == null || chest.getLevel().isClientSide)
				return InteractionResult.SUCCESS;
			BaseDrawerItem.setStackingFactor(chest.handler.config, size + 1);
			chest.sync();
			stack.shrink(1);
			return InteractionResult.SUCCESS;
		}
		return IBagTool.super.clickDrawerBlock(stack, chest);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		list.add(LangData.IDS.UPGRADE.get().withStyle(ChatFormatting.GRAY));
	}
}
