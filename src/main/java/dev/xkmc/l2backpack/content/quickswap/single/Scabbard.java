package dev.xkmc.l2backpack.content.quickswap.single;

import dev.xkmc.l2backpack.content.client.ItemOnBackItem;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.SingleSwapItem;
import dev.xkmc.l2backpack.content.quickswap.common.SingleSwapToken;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2backpack.init.data.LBLang;
import dev.xkmc.l2backpack.init.data.LBTagGen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Scabbard extends SingleSwapItem implements ItemOnBackItem {

	public static boolean isValidItem(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem().canFitInsideContainerItems() && !stack.is(LBTagGen.BACKPACK_BLACKLIST) && !stack.isStackable() &&
				getEquipmentSlotForItem(stack).getType() == EquipmentSlot.Type.HAND;
	}

	public Scabbard(Properties props) {
		super(props.stacksTo(1).fireResistant());
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		LBLang.addInfo(flag, list,
				LBLang.Info.SCABBARD_INFO,
				LBLang.Info.INHERIT);
	}

	@Nullable
	@Override
	public IQuickSwapToken<?> getTokenOfType(ItemStack stack, LivingEntity player, QuickSwapType type) {
		if (type != QuickSwapTypes.TOOL)
			return null;
		return new SingleSwapToken(this, stack, type);
	}

	@Override
	public boolean isValidContent(ItemStack stack) {
		return isValidItem(stack);
	}
}
