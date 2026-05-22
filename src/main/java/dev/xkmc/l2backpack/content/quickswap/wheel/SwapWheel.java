package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.WheelSelectToServer;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapManager;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.wheel.ArcCode;
import dev.xkmc.l2itemselector.wheel.ItemWheel;
import dev.xkmc.l2itemselector.wheel.ItemWheelEntry;
import dev.xkmc.l2itemselector.wheel.WheelAdaptor;
import dev.xkmc.l2itemselector.wheel.WheelContext;
import dev.xkmc.l2itemselector.wheel.WheelHandler;
import dev.xkmc.l2itemselector.wheel.WheelKeyHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import javax.annotation.Nullable;
import java.util.List;

public interface SwapWheel<T extends WheelAdaptor.Entry> extends ItemWheel<T> {

	IQuickSwapToken<?> token();

	@Override
	default WheelKeyHandler getInputHandler() {
		return SwapWheelKeyHandler.INSTANCE;
	}

	@Override
	default ItemStack getItem(List<T> list, int index) {
		if (index < 0 || index >= list.size()) return ItemStack.EMPTY;
		var entry = list.get(index);
		if (entry instanceof ItemWheelEntry e) return e.stack();
		if (entry instanceof ArmorWheelEntry e) return e.stack();
		if (entry instanceof SetSwapWheel.SetWheelEntry e) {
			for (var stack : e.set().asList()) {
				if (!stack.isEmpty()) return stack;
			}
			return ItemStack.EMPTY;
		}
		return ItemStack.EMPTY;
	}

	@Override
	default void renderIcon(GuiGraphics g, int x0, int y0, boolean left, float sideWidth, boolean hover) {
		ItemWheel.super.renderIcon(g, x0, y0, left, sideWidth, hover);
		float cx = left ? sideWidth / 2.0F : (float) g.guiWidth() - sideWidth / 2.0F;
		float r = Math.min((float) x0 / 1.5F, (float) y0) / 1.5F;
		float r0 = Math.min(sideWidth / 2f, r * 0.75f) * (hover ? 0.3f : 0.15f);
		if (r0 > 4) {
			float s = r * 0.025F;
			g.pose().pushPose();
			g.pose().translate(cx, y0, 0.1f);
			g.pose().scale(s, s, s);
			g.renderItem(token().stack(), -8, -8);
			g.pose().popPose();
		}
		if (hover) {
			Component text = token().stack().getHoverName();
			Font font = Minecraft.getInstance().font;
			int cx2 = left ? (int) (sideWidth / 2) : g.guiWidth() - (int) (sideWidth / 2);
			int ty = y0 + (int) r0 * 2;
			for (var line : font.split(text, (int) sideWidth - 4)) {
				g.drawString(font, line, cx2 - font.width(line) / 2, ty, 0xffffff, false);
				ty += font.lineHeight + 1;
			}
		}
	}

	@Override
	default void renderImpl(GuiGraphics g, Player player, List<T> list, WheelContext ctx) {
		ctx.region().render(g, player, list, ctx);
	}

	default void renderBagIcon(GuiGraphics g) {
		int x0 = g.guiWidth() / 2, y0 = g.guiHeight() / 2;
		float r = Math.min(x0 / 1.5f, y0) / 1.5f;
		float s = r * 0.02f;
		g.pose().pushPose();
		g.pose().translate(x0, y0, 0);
		g.pose().scale(s, s, s);
		g.renderItem(token().stack(), -8, -16);
		g.pose().popPose();
	}

	@Nullable
	default Component getSwitchText(WheelContext ctx) {
		int switcher = ctx.code().switcher();
		if (switcher == 0) return null;
		WheelAdaptor<?> adj = switcher == -1 ? ctx.left() : ctx.right();
		if (adj instanceof SwapWheel<?> sw) {
			return sw.token().stack().getHoverName();
		}
		return null;
	}

	default void renderCenter(GuiGraphics g, Player player, List<T> list, WheelContext ctx) {
		renderBagIcon(g);
		int x0 = g.guiWidth() / 2, y0 = g.guiHeight() / 2;
		float r = Math.min(x0 / 1.5f, y0) / 1.5f;
		float s = r * 0.02f;
		Component text = getSwitchText(ctx);
		if (text == null) {
			int index = ctx.hover();
			ItemStack bag = token().stack();
			text = index >= 0 ? getItem(list, index).getHoverName() : bag.getHoverName();
		}
		var font = Minecraft.getInstance().font;
		int ty = (int) (y0 + s * 3);
		for (var line : font.split(text, (int) r)) {
			g.drawString(font, line, x0 - font.width(line) / 2, ty, 0xffffff, false);
			ty += font.lineHeight + 1;
		}
	}

	class SwapWheelKeyHandler implements WheelKeyHandler {

		public static final WheelKeyHandler INSTANCE = new SwapWheelKeyHandler();

		private static int getSelect(WheelAdaptor<?> wheel, Player player) {
			var code = wheel.getMouseSelect(player);
			if (code.sel() < 0) return WheelHandler.keyboardIndex;
			return code.sel();
		}

		@Override
		public void handleClientKey(L2Keys k, Player player) {
			WheelKeyHandler.getDefault().handleClientKey(k, player);
		}

		@Override
		public boolean handleClientScroll(int diff, Player player) {
			return WheelKeyHandler.getDefault().handleClientScroll(diff, player);
		}

		@Override
		public void leftClick(WheelAdaptor<?> wheel, Player player) {
			WheelKeyHandler.getDefault().leftClick(wheel, player);
		}

		@Override
		public void rightClick(WheelAdaptor<?> wheel, Player player) {
			WheelKeyHandler.getDefault().rightClick(wheel, player);
		}

		@Override
		public boolean shouldOpen(boolean longPress) {
			return WheelKeyHandler.getDefault().shouldOpen(longPress);
		}

		@Override
		public boolean onReleaseWithWheel(WheelAdaptor<?> wheel, Player player, boolean longPress, boolean heldWithWheel) {
			if (longPress && wheel instanceof SwapWheel<?> sw) {
				int index = getSelect(wheel, player);
				if (index >= 0) {
					var token = sw.token();
					token.setSelected(index);
					if (token.type() == QuickSwapTypes.ARROW) {
						wheel.select(index);
					}
				}
				return true;
			}
			return WheelKeyHandler.getDefault().onReleaseWithWheel(wheel, player, longPress, heldWithWheel);
		}

		@Override
		public void onReleaseWithoutWheel(WheelAdaptor<?> sel, Player player, boolean longPress) {
			if (!longPress && sel instanceof SwapWheel<?> wheel) {
				var token = wheel.token();
				if (token.type() == QuickSwapTypes.ARROW && player.getMainHandItem().getItem() instanceof ProjectileWeaponItem) {
					var list = QuickSwapManager.getWheelTokens(player, L2Keys.hasShiftDown());
					for (int i = 0; i < list.size(); i++) {
						var t = list.get(i);
						if (t.type() == QuickSwapTypes.TOOL && t.type().supportWheel()) {
							int selected = t.getSelected();
							L2Backpack.HANDLER.toServer(new WheelSelectToServer(selected, i, L2Keys.hasShiftDown()));
							return;
						}
					}
				}
			}
			WheelKeyHandler.getDefault().onReleaseWithoutWheel(sel, player, longPress);
		}

		@Override
		public ArcCode getArcColor(WheelContext ctx) {
			return WheelKeyHandler.getDefault().getArcColor(ctx);
		}

	}

}
