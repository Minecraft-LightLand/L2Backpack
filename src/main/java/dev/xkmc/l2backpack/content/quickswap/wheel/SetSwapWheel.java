package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.SetSwapToken;
import dev.xkmc.l2backpack.content.quickswap.entry.SetSwapEntry;
import dev.xkmc.l2backpack.content.quickswap.type.ArmorSwapType;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2itemselector.overlay.WheelAdaptor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record SetSwapWheel(
		SetSwapToken token, int wheelIndex, ItemStack prev, ItemStack next
) implements WheelAdaptor, IQuickSwapToken.SwapWheel {

	public List<Entry> getWheelContent() {
		var src = token.getList();
		ArrayList<Entry> ans = new ArrayList<>();
		for (var e : src) {
			ans.add(new SetWheelEntry(e));
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

		if (ma >= 0) {
			var entries = getWheelContent();
			if (ma < entries.size() && entries.get(ma) instanceof SetWheelEntry entry) {
				var setItems = entry.set().asList();
				int ty = y0 + 2;
				int startX = x0 - 34;
				var type = QuickSwapTypes.ARMOR;
				for (int i = 0; i < 4; i++) {
					if (i < setItems.size()) {
						EquipmentSlot e = ArmorWheelEntry.getSlot(i);
						ItemStack old = player.getItemBySlot(e);
						ItemStack cur = setItems.get(i);
						boolean avail = type.maySwapOut(old) && (!old.isEmpty() || !cur.isEmpty());
						ArmorSwapType.renderArmorSlot(g, startX + i * 17, ty, 64, !entry.set().isLocked(i) && (!old.isEmpty() || !cur.isEmpty()), !avail);
						g.renderItem(cur, startX + i * 17, ty);
					}
				}
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

	public record SetWheelEntry(SetSwapEntry set) implements WheelAdaptor.Entry {

		public void render(GuiGraphics g, float x0, float y0, float ai, float r0, float r, float da, float s) {
			s *= Math.min(r * 0.015F, da * r0 / 44.0F) / 1.2f;
			float dx = x0 + Mth.cos(ai) * r0;
			float dy = y0 + Mth.sin(ai) * r0;
			g.pose().pushPose();
			g.pose().translate(dx, dy, 0.0F);
			g.pose().scale(s, s, s);
			for (int i = 0; i < 4; i++) {
				if (set.list().size() <= i) continue;
				var stack = set.list().get(i);
				if (stack.isEmpty()) continue;
				g.renderItem(stack, i % 2 == 0 ? -16 : 0, i <= 1 ? -16 : 0);
			}
			g.pose().popPose();
		}

	}

}
