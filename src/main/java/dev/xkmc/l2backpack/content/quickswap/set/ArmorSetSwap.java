package dev.xkmc.l2backpack.content.quickswap.set;

import dev.xkmc.l2backpack.content.client.ItemOnBackItem;
import dev.xkmc.l2backpack.content.quickswap.common.*;
import dev.xkmc.l2backpack.content.quickswap.single.ArmorSwap;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArmorSetSwap extends SetSwapItem implements ItemOnBackItem {

	public ArmorSetSwap(Properties props) {
		super(props, 4);
	}

	@Override
	public void open(ServerPlayer player, PlayerSlot<?> slot, ItemStack stack) {
		new SimpleMenuPvd(player, slot, this, stack, ArmorSetBagMenu::new).open();
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		LBLang.addInfo(flag, list, LBLang.Info.SUIT_BAG_INFO, LBLang.Info.INHERIT);
	}

	@Nullable
	@Override
	public IQuickSwapToken<?> getTokenOfType(ItemStack stack, LivingEntity player, QuickSwapType type) {
		if (type != QuickSwapTypes.ARMOR)
			return null;
		return new SetSwapToken(this, stack, type);
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		var e = SingleSwapItem.getEquipmentSlotForItem(stack);
		if (e.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) return false;
		return slot / 9 + e.ordinal() == 5;
	}

	@Override
	public boolean isValidContent(ItemStack stack) {
		return ArmorSwap.isValidItem(stack);
	}

}
