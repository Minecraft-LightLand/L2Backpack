package dev.xkmc.l2backpack.content.quickswap.type;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.common.SingleSwapItem;
import dev.xkmc.l2backpack.content.quickswap.entry.*;
import dev.xkmc.l2backpack.init.data.LBConfig;
import dev.xkmc.l2core.init.reg.ench.EnchHelper;
import dev.xkmc.l2itemselector.overlay.OverlayUtil;
import dev.xkmc.l2itemselector.overlay.SelectionSideBar;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

public class ArmorSwapType extends QuickSwapType
		implements ISideInfoRenderer, ISingleSwapAction, ISetSwapAction {

	public ArmorSwapType(String name, int index) {
		super(name, index);
	}

	@Override
	public boolean activePopup() {
		return LBConfig.CLIENT.popupArmorOnSwitch.get();
	}

	@Override
	public ItemStack getSignatureItem(Player player) {
		return ItemStack.EMPTY;
	}

	public boolean maySwapOut(ItemStack stack) {
		if (EnchHelper.getLv(stack, Enchantments.BINDING_CURSE) > 0) return false;
		return stack.getItem().canFitInsideContainerItems() &&
				!(stack.getItem() instanceof BaseBagItem);
	}

	private EquipmentSlot getSlot(int i) {
		return EquipmentSlot.values()[5 - i];
	}

	@Override
	public void swapSingle(Player player, ISingleSwapHandler handler) {
		ItemStack stack = handler.getStack();
		if (stack.isEmpty()) return;
		EquipmentSlot slot = SingleSwapItem.getEquipmentSlotForItem(stack);
		if (!maySwapOut(player.getItemBySlot(slot))) return;
		handler.replace(player.getItemBySlot(slot));
		player.setItemSlot(slot, stack);
	}

	@Override
	public void swapSet(Player player, ISetSwapHandler handler) {
		for (int i = 0; i < 4; i++) {
			EquipmentSlot e = getSlot(i);
			if (handler.isLocked(i))
				continue;
			if (!maySwapOut(player.getItemBySlot(e)))
				continue;
			ItemStack stack = handler.getStack(i);
			handler.replace(i, player.getItemBySlot(e));
			player.setItemSlot(e, stack);
		}
	}

	@Override
	public boolean isAvailable(Player player, ISwapEntry<?> token) {
		if (token instanceof SingleSwapEntry single) {
			ItemStack stack = single.stack();
			if (stack.isEmpty()) return false;
			EquipmentSlot slot = SingleSwapItem.getEquipmentSlotForItem(stack);
			return maySwapOut(player.getItemBySlot(slot));
		}
		if (token instanceof SetSwapEntry) {
			for (int i = 0; i < 4; i++) {
				if (isAvailable(player, token, i))
					return true;
			}
			return false;
		}
		return false;
	}


	public boolean isAvailable(Player player, ISwapEntry<?> token, int index) {
		EquipmentSlot e = getSlot(index);
		ItemStack old = player.getItemBySlot(e);
		ItemStack cur = token.asList().get(index);
		return maySwapOut(old) && (!old.isEmpty() || !cur.isEmpty());
	}

	public void renderSide(SelectionSideBar.Context ctx, int x, int y, Player player, ISwapEntry<?> token) {
		y -= 36;
		if (token instanceof SingleSwapEntry single) {
			ItemStack hover = single.stack();
			EquipmentSlot target = SingleSwapItem.getEquipmentSlotForItem(hover);
			for (int i = 0; i < 4; i++) {
				EquipmentSlot slot = getSlot(i);
				ItemStack stack = player.getItemBySlot(slot);
				ItemStack targetStack = player.getItemBySlot(target);
				renderArmorSlot(ctx.g(), x, y, 64, target == slot, !maySwapOut(targetStack));
				ctx.renderItem(stack, x, y);
				y += 18;
			}
		}
		if (token instanceof SetSwapEntry set) {
			for (int i = 0; i < 4; i++) {
				EquipmentSlot e = getSlot(i);
				ItemStack old = player.getItemBySlot(e);
				ItemStack cur = set.asList().get(i);
				boolean avail = maySwapOut(old) && (!old.isEmpty() || !cur.isEmpty());
				renderArmorSlot(ctx.g(), x, y, 64, !set.isLocked(i) && (!old.isEmpty() || !cur.isEmpty()), !avail);
				ctx.renderItem(old, x, y);
				y += 18;
			}
		}
	}

	public static void renderArmorSlot(GuiGraphics g, int x, int y, int a, boolean target, boolean invalid) {
		OverlayUtil.fillRect(g, x, y, 16, 16, color(255, 255, 255, a));
		if (target) {
			if (invalid) {
				OverlayUtil.drawRect(g, x, y, 16, 16, color(220, 70, 70, 255));
			} else {
				OverlayUtil.drawRect(g, x, y, 16, 16, color(70, 150, 185, 255));
			}
		}
	}

}
