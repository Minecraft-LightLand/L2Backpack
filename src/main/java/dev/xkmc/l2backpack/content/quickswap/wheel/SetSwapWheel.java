package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.SetSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.WheelSelectToServer;
import dev.xkmc.l2backpack.content.quickswap.entry.SetSwapEntry;
import dev.xkmc.l2backpack.content.quickswap.type.ArmorSwapType;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.overlay.WheelAdaptor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record SetSwapWheel(
		SetSwapToken token, int wheelIndex, ItemStack next
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

	public void render(GuiGraphics g, Player player) {
		WheelAdaptor.super.render(g, player);
		ItemStack stack = token.stack();
		int x0 = g.guiWidth() / 2;
		int y0 = g.guiHeight() / 2;
		float r = (float) Math.min(x0, y0) / 2.0F;
		float s = r * 0.03F;
		g.pose().pushPose();
		g.pose().translate((float) x0, (float) y0, 0.0F);
		g.pose().scale(s, s, s);
		var sel = getMouseSelect(player);
		if (sel < 0) g.renderItem(stack, -8, -8);
		if (!next.isEmpty()) {
			g.pose().translate(12, -4, 0);
			g.pose().scale(0.5f, 0.5f, 0.5f);
			g.renderItem(next, -8, -8);
		}
		g.pose().popPose();
	}

	@Override
	public void select(int i) {
		L2Backpack.HANDLER.toServer(new WheelSelectToServer(i, wheelIndex, L2Keys.hasShiftDown()));
	}

	public record SetWheelEntry(SetSwapEntry set) implements WheelAdaptor.Entry {

		public void render(GuiGraphics g, float x0, float y0, float ai, float r0, float r, float da, float s) {
			boolean sel = s > 1;
			s *= Math.min(r * 0.015F, da * r0 / 44.0F);
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
			if (!sel) return;
			Player player = Minecraft.getInstance().player;
			if (player == null) return;
			var type = QuickSwapTypes.ARMOR;
			for (int i = 0; i < 4; i++) {
				int x = (int) x0 + (i % 2 == 0 ? -17 : 1), y = (int) y0 + (i <= 1 ? -17 : 1);
				EquipmentSlot e = ArmorWheelEntry.getSlot(i);
				ItemStack old = player.getItemBySlot(e);
				ItemStack cur = set.asList().get(i);
				boolean avail = type.maySwapOut(old) && (!old.isEmpty() || !cur.isEmpty());
				ArmorSwapType.renderArmorSlot(g, x, y, 64, !set.isLocked(i) && (!old.isEmpty() || !cur.isEmpty()), !avail);
				g.renderItem(old, x, y);
			}
		}



	}

}
