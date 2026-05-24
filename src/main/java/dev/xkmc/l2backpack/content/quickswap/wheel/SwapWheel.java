package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapOverlay;
import dev.xkmc.l2backpack.content.quickswap.common.WheelSelectToServer;
import dev.xkmc.l2backpack.events.BackpackSel;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2itemselector.init.data.L2ISConfig;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.wheel.ItemWheel;
import dev.xkmc.l2itemselector.wheel.WheelAdaptor;
import dev.xkmc.l2itemselector.wheel.WheelContext;
import dev.xkmc.l2itemselector.wheel.WheelKeyHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface SwapWheel<T extends WheelAdaptor.Entry> extends ItemWheel<T> {

	IQuickSwapToken<?> token();

	int wheelIndex();

	@Override
	default WheelKeyHandler getInputHandler() {
		return SwapWheelKeyHandler.getDefault();
	}

	@Override
	default void select(int i) {
		L2Backpack.HANDLER.toServer(new WheelSelectToServer(i, wheelIndex(), L2Keys.hasShiftDown()));
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
			int cx2 = left ? (int) (sideWidth / 2) : g.guiWidth() - (int) (sideWidth / 2);
			int ty = y0 + (int) r0 * 2;
			renderText(g, token().stack().getHoverName(), cx2, ty, sideWidth - 4);
		}
	}

	default void renderText(GuiGraphics g, Component text, int x0, int textY, float r) {
		Font font = Minecraft.getInstance().font;
		int ty = textY;
		for (var line : font.split(text, (int) r)) {
			g.drawString(font, line, x0 - font.width(line) / 2, ty, 0xffffff, true);
			ty += font.lineHeight + 1;
		}
	}

	@Override
	default void renderImpl(GuiGraphics g, Player player, List<T> list, WheelContext ctx) {
		ctx.region().render(g, player, list, ctx);
		renderBagIcon(g);
		if (ctx.code().switcher() != 0 && !L2ISConfig.CLIENT.useFastSwitchWheel.get()) return;
		renderCenter(g, player, list, ctx);
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

	default void renderCenter(GuiGraphics g, Player player, List<T> list, WheelContext ctx) {
		int x0 = g.guiWidth() / 2, y0 = g.guiHeight() / 2;
		float r = Math.min(x0 / 1.5f, y0) / 1.5f;
		float s = r * 0.02f;
		int index = ctx.hover();
		ItemStack display = token().stack();
		if (index >= 0) {
			var content = getItem(list, index);
			if (!content.isEmpty()) display = content;
		}
		Component text = display.getHoverName();
		var font = Minecraft.getInstance().font;
		int ty = (int) (y0 + s * 3);
		for (var line : font.split(text, (int) r)) {
			g.drawString(font, line, x0 - font.width(line) / 2, ty, 0xffffff, true);
			ty += font.lineHeight + 1;
		}
		if (index >= 0) {
			var contents = display.get(DataComponents.POTION_CONTENTS);
			if (contents != null) {
				var effects = new ArrayList<MobEffectInstance>();
				for (MobEffectInstance e : contents.getAllEffects()) {
					effects.add(e);
				}
				for (MobEffectInstance effect : effects) {
					Component effectName = effect.getEffect().value().getDisplayName();
					int amp = effect.getAmplifier();
					if (amp > 0) {
						effectName = Component.translatable("potion.withAmplifier", effectName,
								Component.translatable("enchantment.level." + (amp + 1)));
					}
					effectName = Component.translatable("potion.withDuration", effectName,
							MobEffectUtil.formatDuration(effect, 0.125F, 20.0F));
					for (var line : font.split(effectName, (int) (r * 0.8))) {
						g.drawString(font, line, x0 - font.width(line) / 2, ty, 0x8888FF, true);
						ty += font.lineHeight + 1;
					}
				}
			}
		}
	}

	@Override
	default void onClose() {
		BackpackSel.clicked = false;
		BackpackSel.prevType = null;
		QuickSwapOverlay.suppress();
	}

	@Override
	default void onOpen() {
	}

	@Override
	default void onSwitchedAway() {
		BackpackSel.clicked = false;
	}

}
