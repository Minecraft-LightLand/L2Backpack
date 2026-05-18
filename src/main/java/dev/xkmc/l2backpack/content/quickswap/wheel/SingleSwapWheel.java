package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.SingleSwapToken;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapManager;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.overlay.ItemWheelEntry;
import dev.xkmc.l2itemselector.overlay.WheelAdaptor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import java.util.ArrayList;
import java.util.List;

public record SingleSwapWheel(SingleSwapToken token, int wheelIndex,
                              ItemStack prev, ItemStack next) implements WheelAdaptor, IQuickSwapToken.SwapWheel {

	public List<Entry> getWheelContent() {
		List<ItemStack> src = token.getRawList();
		ArrayList<Entry> ans = new ArrayList<>();
		for (ItemStack e : src) {
			ans.add(new ItemWheelEntry(e));
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
		int ty = (int) (y0 + s * 3);
		Component text;
		if (ma >= 0) {
			var list = token.getRawList();
			text = ma < list.size() ? list.get(ma).getHoverName() : pouch.getHoverName();
		} else {
			text = pouch.getHoverName();
		}
		for (var line : font.split(text, (int) r)) {
			g.drawString(font, line, x0 - font.width(line) / 2, ty, 0xffffff, false);
			ty += font.lineHeight + 1;
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
		if (token.type() == QuickSwapTypes.ARROW) {
			SwapWheelUtil.select(i, wheelIndex);
		}
	}

	@Override
	public void shortPress(Player player) {
		if (token.type() == QuickSwapTypes.ARROW && player.getMainHandItem().getItem() instanceof ProjectileWeaponItem) {
			var list = QuickSwapManager.getWheelTokens(player, L2Keys.hasShiftDown());
			for (int i = 0; i < list.size(); i++) {
				var t = list.get(i);
				if (t.type() == QuickSwapTypes.TOOL && t.type().supportWheel()) {
					SwapWheelUtil.shortPress(t.getSelected(), i);
					return;
				}
			}
		}
		SwapWheelUtil.shortPress(token.getSelected(), wheelIndex);
	}

}
