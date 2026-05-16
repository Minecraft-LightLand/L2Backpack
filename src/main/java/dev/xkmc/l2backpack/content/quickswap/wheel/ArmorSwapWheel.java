package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.SingleSwapItem;
import dev.xkmc.l2backpack.content.quickswap.common.SingleSwapToken;
import dev.xkmc.l2backpack.content.quickswap.type.ArmorSwapType;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2itemselector.overlay.WheelAdaptor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ArmorSwapWheel(SingleSwapToken token, int wheelIndex,
                      ItemStack prev, ItemStack next) implements WheelAdaptor, IQuickSwapToken.SwapWheel {

	public List<Entry> getWheelContent() {
		List<ItemStack> src = token.getRawList();
		ArrayList<Entry> ans = new ArrayList<>();
		for (ItemStack e : src) {
			ans.add(new ArmorWheelEntry(e));
		}

		return ans;
	}

	public int getIndex(Player player) {
		return token.getSelected();
	}

	public int render(GuiGraphics g, Player player) {
		int ma = WheelAdaptor.super.render(g, player);
		int x0 = g.guiWidth() / 2;
		int y0 = g.guiHeight() / 2;
		float r = Math.min(x0, y0) / 2f;
		float s = r * 0.02f;

		ItemStack pouch = token.stack();
		g.pose().pushPose();
		g.pose().translate(x0, y0, 0);
		g.pose().scale(s, s, s);
		g.renderItem(pouch, -8, -16);
		g.pose().popPose();

		var font = Minecraft.getInstance().font;
		var type = QuickSwapTypes.ARMOR;
		var clientPlayer = Minecraft.getInstance().player;

		if (ma >= 0 && clientPlayer != null) {
			var list = token.getRawList();
			ItemStack hovered = ma < list.size() ? list.get(ma) : ItemStack.EMPTY;
			EquipmentSlot target = SingleSwapItem.getEquipmentSlotForItem(hovered);
			int ty = y0 + 2;
			int startX = x0 - 34;
			for (int i = 0; i < 4; i++) {
				EquipmentSlot slot = ArmorWheelEntry.getSlot(i);
				ItemStack equipped = clientPlayer.getItemBySlot(slot);
				ItemStack targetStack = clientPlayer.getItemBySlot(target);
				ArmorSwapType.renderArmorSlot(g, startX + i * 17, ty, 64, target == slot, !type.maySwapOut(targetStack));
				g.renderItem(equipped, startX + i * 17, ty);
			}
		} else {
			Component text = pouch.getHoverName();
			int ty = (int) (y0 + s * 3);
			for (var line : font.split(text, (int) r)) {
				g.drawString(font, line, x0 - font.width(line) / 2, ty, 0xffffff, false);
				ty += font.lineHeight + 1;
			}
		}

		SwapWheelUtil.renderSideItems(g, prev, next);
		return ma;
	}

	@Override
	public void select(int i) {
		SwapWheelUtil.select(i, wheelIndex);
	}

	@Override
	public void onRelease(int i) {
		token.setSelected(i);
	}

	@Override
	public void shortPress(Player player) {
		SwapWheelUtil.shortPress(token.getSelected(), wheelIndex);
	}

}
