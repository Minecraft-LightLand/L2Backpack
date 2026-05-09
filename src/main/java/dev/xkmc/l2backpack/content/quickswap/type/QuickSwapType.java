package dev.xkmc.l2backpack.content.quickswap.type;

import dev.xkmc.l2backpack.content.quickswap.common.EntryRenderContext;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapOverlay;
import dev.xkmc.l2backpack.content.quickswap.entry.ISwapEntry;
import dev.xkmc.l2itemselector.overlay.OverlayUtil;
import dev.xkmc.l2itemselector.overlay.SelectionSideBar;
import dev.xkmc.l2itemselector.overlay.TextBox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

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

	public boolean isAvailable(Player player, ISwapEntry<?> token) {
		return true;
	}

	public boolean isAvailable(Player player, ISwapEntry<?> token, int index) {
		return isAvailable(player, token);
	}

	public void renderSelected(SelectionSideBar.Context ctx, Player player, ISwapEntry<?> token, EntryRenderContext entry) {
		List<ItemStack> list = token.asList();
		boolean shift = QuickSwapOverlay.INSTANCE.isOnHold();
		boolean avail = isAvailable(player, token);
		for (int i = 0; i < list.size(); i++) {
			if (token.isLocked(i)) continue;
			renderSelection(ctx.g(), entry.x() + i * 18, entry.y(), shift ? 127 : 64, avail,
					(!avail || isAvailable(player, token, i)) && entry.selected());
		}
		if (entry.selected() && list.size() == 1) {
			ItemStack stack = list.getFirst();
			if (!stack.isEmpty()) {
				ctx.g().renderTooltip(ctx.font(), stack.getHoverName(), 0, 0);
				TextBox box = new TextBox(ctx.g(), entry.center() ? 0 : 2, 1,
						ctx.x0() + (entry.center() ? 22 : -6), entry.y() + 8, -1);
				box.renderLongText(ctx.font(), List.of(stack.getHoverName()));
			}
		}
		for (int i = 0; i < list.size(); i++) {
			if (token.isLocked(i)) continue;
			ctx.renderItem(list.get(i), entry.x() + i * 18, entry.y());
		}
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

	public boolean supportWheel() {
		return true;
	}

}
