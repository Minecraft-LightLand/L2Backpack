package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.common.SetSwapToken;
import dev.xkmc.l2backpack.content.quickswap.type.ArmorSwapType;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2itemselector.init.data.L2ISConfig;
import dev.xkmc.l2itemselector.wheel.WheelContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record SetSwapWheel(
		SetSwapToken token, int wheelIndex
) implements SwapWheel<SetWheelEntry> {

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
	public ItemStack getItem(List<SetWheelEntry> list, int index) {
		if (index < 0 || index >= list.size()) return ItemStack.EMPTY;
		var entry = list.get(index);
		for (var stack : entry.set().asList()) {
			if (!stack.isEmpty()) return stack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void renderCenter(GuiGraphics g, Player player, List<SetWheelEntry> list, WheelContext ctx) {
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
		}
	}

}
