package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.common.SingleSwapItem;
import dev.xkmc.l2backpack.content.quickswap.common.SingleSwapToken;
import dev.xkmc.l2backpack.content.quickswap.type.ArmorSwapType;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2itemselector.init.data.L2ISConfig;
import dev.xkmc.l2itemselector.wheel.WheelContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ArmorSwapWheel(
		SingleSwapToken token, int wheelIndex
) implements SwapWheel<ArmorWheelEntry> {

	public List<ArmorWheelEntry> getWheelContent() {
		List<ItemStack> src = token.getRawList();
		ArrayList<ArmorWheelEntry> ans = new ArrayList<>();
		for (ItemStack e : src) {
			ans.add(new ArmorWheelEntry(e));
		}
		return ans;
	}

	public int getIndex(Player player) {
		return token.getSelected();
	}

	@Override
	public ItemStack getItem(List<ArmorWheelEntry> list, int index) {
		if (index < 0 || index >= list.size()) return ItemStack.EMPTY;
		return list.get(index).stack();
	}

	@Override
	public void renderCenter(GuiGraphics g, Player player, List<ArmorWheelEntry> list, WheelContext ctx) {
		int x0 = g.guiWidth() / 2, y0 = g.guiHeight() / 2;
		float r = Math.min(x0 / 1.5f, y0) / 1.5f;
		float s = r * 0.02f;
		int textY = (int) (y0 + s * 3);
		float armorScale = r * 0.01f;
		int armorY = y0 + (int) (s * 1 * armorScale);
		int index = ctx.hover();
		if (index < 0 || index >= list.size()) {
			renderText(g, token.stack().getHoverName(), x0, textY, r);
		} else {
			ItemStack hovered = list.get(index).stack();
			EquipmentSlot target = LivingEntity.getEquipmentSlotForItem(hovered);
			var type = QuickSwapTypes.ARMOR;
			g.pose().pushPose();
			g.pose().translate(x0, armorY, 0.1f);
			g.pose().scale(armorScale, armorScale, 1);
			for (int i = 0; i < 4; i++) {
				EquipmentSlot slot = ArmorWheelEntry.getSlot(i);
				ItemStack equipped = player.getItemBySlot(slot);
				ItemStack targetStack = player.getItemBySlot(target);
				int sx = (i - 2) * 17;
				ArmorSwapType.renderArmorSlot(g, sx, 0, 64, target == slot, !type.maySwapOut(targetStack));
				g.renderItem(equipped, sx, 0);
			}
			g.pose().popPose();
		}
	}

}
