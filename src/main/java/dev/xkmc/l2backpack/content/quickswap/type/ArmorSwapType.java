package dev.xkmc.l2backpack.content.quickswap.type;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.init.data.BackpackConfig;
import dev.xkmc.l2library.base.overlay.OverlayUtil;
import dev.xkmc.l2library.base.overlay.SelectionSideBar;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class ArmorSwapType extends QuickSwapType implements SideInfoRenderer {

	public ArmorSwapType(String name, int index) {
		super(name, index);
	}

	@Override
	public boolean activePopup() {
		return BackpackConfig.CLIENT.popupArmorOnSwitch.get();
	}

	@Override
	public ItemStack getSignatureItem(Player player) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canSwap() {
		return true;
	}

	private boolean test(ItemStack stack) {
		return stack.getItem().canFitInsideContainerItems() &&
				!(stack.getItem() instanceof BaseBagItem);
	}

	@Override
	public void swap(Player player, ItemStack stack, Consumer<ItemStack> cons) {
		if (stack.isEmpty()) return;
		EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(stack);
		if (!test(player.getItemBySlot(slot))) return;
		cons.accept(player.getItemBySlot(slot));
		player.setItemSlot(slot, stack);
	}

	@Override
	public boolean isAvailable(Player player, OverlayToken<?> token) {
		if (!(token instanceof SingleOverlayToken single)) return false;
		ItemStack stack = single.stack();//TODO
		if (stack.isEmpty()) return false;
		EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(stack);
		return test(player.getItemBySlot(slot));
	}

	public void renderSide(SelectionSideBar.Context ctx, int x, int y, Player player, OverlayToken<?> token) {
		if (!(token instanceof SingleOverlayToken single)) return;
		ItemStack hover = single.stack();//TODO
		EquipmentSlot target = LivingEntity.getEquipmentSlotForItem(hover);
		for (int i = 0; i < 4; i++) {
			EquipmentSlot slot = EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, 3 - i);
			ItemStack stack = player.getItemBySlot(slot);
			ItemStack targetStack = player.getItemBySlot(target);
			renderArmorSlot(ctx.g(), x, y, 64, target == slot, !test(targetStack));
			ctx.renderItem(stack, x, y);
			y += 18;
		}
	}

	private static void renderArmorSlot(GuiGraphics g, int x, int y, int a, boolean target, boolean invalid) {
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
