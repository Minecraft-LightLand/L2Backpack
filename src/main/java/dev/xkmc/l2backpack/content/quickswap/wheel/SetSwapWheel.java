package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.common.SetSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.WheelSelectToServer;
import dev.xkmc.l2backpack.content.quickswap.entry.SetSwapEntry;
import dev.xkmc.l2backpack.content.quickswap.type.ArmorSwapType;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.wheel.WheelAdaptor;
import dev.xkmc.l2itemselector.wheel.WheelContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record SetSwapWheel(
		SetSwapToken token, int wheelIndex
) implements SwapWheel<SetSwapWheel.SetWheelEntry> {

	public List<SetWheelEntry> getWheelContent() {
		var src = token.getList();
		ArrayList<SetWheelEntry> ans = new ArrayList<>();
		for (var e : src) {
			ans.add(new SetWheelEntry(e));
		}
		return ans;
	}

	public int getIndex(Player player) {
		return token.getSelected();
	}

	@Override
	public void renderImpl(GuiGraphics g, Player player, List<SetWheelEntry> list, WheelContext ctx) {
		SwapWheel.super.renderImpl(g, player, list, ctx);
		renderBagIcon(g);
		int x0 = g.guiWidth() / 2, y0 = g.guiHeight() / 2;
		float r = Math.min(x0 / 1.5f, y0) / 1.5f;
		float s = r * 0.02f;
		int textY = (int) (y0 + s * 3);
		float armorScale = r * 0.01f;
		int armorY = y0 + (int) (s * 1 * armorScale);
		Component switchText = getSwitchText(ctx);
		if (switchText != null) {
			var font = Minecraft.getInstance().font;
			int ty = textY;
			for (var line : font.split(switchText, (int) r)) {
				g.drawString(font, line, x0 - font.width(line) / 2, ty, 0xffffff, false);
				ty += font.lineHeight + 1;
			}
		} else {
			int index = ctx.hover();
			if (index >= 0 && index < list.size()) {
				var entry = list.get(index);
				var setItems = entry.set().asList();
				var type = QuickSwapTypes.ARMOR;
				g.pose().pushPose();
				g.pose().translate(x0, armorY, 0.1f);
				g.pose().scale(armorScale, armorScale, 1);
				for (int i = 0; i < 4; i++) {
					EquipmentSlot e = ArmorWheelEntry.getSlot(i);
					ItemStack equipped = player.getItemBySlot(e);
					ItemStack targetStack = i < setItems.size() ? setItems.get(i) : ItemStack.EMPTY;
					boolean highlight = !entry.set().isLocked(i) && !targetStack.isEmpty();
					boolean valid = !type.maySwapOut(equipped) && !equipped.isEmpty();
					int sx = (i - 2) * 17;
					ArmorSwapType.renderArmorSlot(g, sx, 0, 64, highlight, valid);
					g.renderItem(equipped, sx, 0);
				}
				g.pose().popPose();
			} else {
				Component text = token.stack().getHoverName();
				Font font = Minecraft.getInstance().font;
				int ty = textY;
				for (var line : font.split(text, (int) r)) {
					g.drawString(font, line, x0 - font.width(line) / 2, ty, 0xffffff, false);
					ty += font.lineHeight + 1;
				}
			}
		}
	}

	@Override
	public void select(int i) {
		L2Backpack.HANDLER.toServer(new WheelSelectToServer(i, wheelIndex, L2Keys.hasShiftDown()));
	}

	public record SetWheelEntry(SetSwapEntry set) implements WheelAdaptor.Entry {

		public void render(GuiGraphics g, float x0, float y0, float ai, float r0, float r, float da, boolean sel) {
			float s = (sel ? 1.1f : 1) * Math.min(r * 0.015F, da * r0 / 44.0F);
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
