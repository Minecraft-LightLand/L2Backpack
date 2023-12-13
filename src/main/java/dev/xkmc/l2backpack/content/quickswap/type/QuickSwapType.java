package dev.xkmc.l2backpack.content.quickswap.type;

import dev.xkmc.l2library.base.overlay.OverlayUtil;
import dev.xkmc.l2library.base.overlay.SelectionSideBar;
import dev.xkmc.l2library.base.overlay.TextBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public abstract class QuickSwapType {

	private final String name;
	private final int index;

	QuickSwapType(String name, int index) {
		this.name = name;
		this.index = index;
	}

	public QuickSwapType(String name) {
		this.name = name;
		index = QuickSwapTypes.register(this);
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public abstract boolean activePopup();

	public abstract ItemStack getSignatureItem(Player player);

	public void swap(Player player, ItemStack stack, Consumer<ItemStack> cons) {
	}

	public boolean canSwap() {
		return false;
	}

	public boolean isAvailable(Player player, OverlayToken<?> token) {
		return true;
	}

	public void renderSelected(SelectionSideBar.Context ctx, Player player, OverlayToken<?> token, int x, int y, boolean selected, boolean center) {
		List<ItemStack> list = token.asList();
		boolean shift = Minecraft.getInstance().options.keyShift.isDown();
		boolean avail = this.isAvailable(player, token);
		for (int i = 0; i < list.size(); i++)
			renderSelection(ctx.g(), x + i * 18, y, shift ? 127 : 64, avail, selected);
		if (selected && list.size() == 1) {
			ItemStack stack = list.get(0);
			if (!stack.isEmpty()) {
			ctx.g().renderTooltip(ctx.font(), stack.getHoverName(), 0, 0);
			TextBox box = new TextBox(ctx.g(), center ? 0 : 2, 1, ctx.x0() + (center ? 22 : -6), y + 8, -1);
				box.renderLongText(ctx.font(), List.of(stack.getHoverName()));
			}
		}
		for (int i = 0; i < list.size(); i++)
			ctx.renderItem(list.get(i), x + i * 18, y);
	}

	public static void renderSelection(GuiGraphics g, int x, int y, int a, boolean available, boolean selected) {
		if (available) {
			OverlayUtil.fillRect(g, x, y, 16, 16, color(255, 255, 255, a));
		} else {
			OverlayUtil.fillRect(g, x, y, 16, 16, color(255, 0, 0, a));
		}
		if (selected) {
			OverlayUtil.drawRect(g, x, y, 16, 16, color(255, 170, 0, 255));
		}
	}

	public static int color(int r, int g, int b, int a) {
		return a << 24 | r << 16 | g << 8 | b;
	}

}
