package dev.xkmc.l2backpack.content.quickswap.single;

import dev.xkmc.l2backpack.content.client.ItemOnBackItem;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.common.*;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2backpack.init.data.LBLang;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArmorSwap extends SingleSwapItem implements ItemOnBackItem {

	public static boolean isValidItem(ItemStack stack) {
		return stack.getItem().canFitInsideContainerItems() && !(stack.getItem() instanceof BaseBagItem) &&
				getEquipmentSlotForItem(stack).getType() == EquipmentSlot.Type.HUMANOID_ARMOR;
	}

	public ArmorSwap(Properties props) {
		super(props.stacksTo(1).fireResistant());
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		LBLang.addInfo(flag, list,
				LBLang.Info.ARMORBAG_INFO,
				LBLang.Info.INHERIT);
	}

	@Nullable
	@Override
	public IQuickSwapToken<?> getTokenOfType(ItemStack stack, LivingEntity player, QuickSwapType type) {
		if (type != QuickSwapTypes.ARMOR)
			return null;
		return new SingleSwapToken(this, stack, type);
	}

	@Override
	public boolean isValidContent(ItemStack stack) {
		return isValidItem(stack);
	}
}
