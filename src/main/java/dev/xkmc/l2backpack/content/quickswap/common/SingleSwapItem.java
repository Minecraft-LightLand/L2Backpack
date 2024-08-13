package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;

public abstract class SingleSwapItem extends BaseBagItem implements IQuickSwapItem {

	public static EquipmentSlot getEquipmentSlotForItem(ItemStack stack) {
		final EquipmentSlot slot = stack.getEquipmentSlot();
		if (slot != null) return slot;
		Equipable equipable = Equipable.get(stack);
		if (equipable != null) return equipable.getEquipmentSlot();
		return EquipmentSlot.MAINHAND;
	}

	public static void setSelected(ItemStack stack, int i) {
		int slot = i;
		if (i < 0) {
			slot = getSelected(stack);
			if (i == -1) slot--;
			else slot++;
			slot = (slot + 9) % 9;
		}
		LBItems.DC_SEL.set(stack, slot);
	}

	public static int getSelected(ItemStack stack) {
		return Mth.clamp(LBItems.DC_SEL.getOrDefault(stack, 0), 0, 8);
	}

	public SingleSwapItem(Properties props) {
		super(props.stacksTo(1).fireResistant());
	}

	@Override
	public void open(ServerPlayer player, PlayerSlot<?> slot, ItemStack stack) {
		new SimpleMenuPvd(player, slot, this, stack, GenericSwapMenu::new).open();
	}

}
