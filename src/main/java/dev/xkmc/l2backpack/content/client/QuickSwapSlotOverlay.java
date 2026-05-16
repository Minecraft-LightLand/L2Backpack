package dev.xkmc.l2backpack.content.client;

import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapManager;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2core.util.Proxy;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import java.util.List;

public class QuickSwapSlotOverlay implements LayeredDraw.Layer {

	public static final QuickSwapSlotOverlay INSTANCE = new QuickSwapSlotOverlay();
	private static final ResourceLocation SLOT = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_left");

	@Override
	public void render(GuiGraphics g, DeltaTracker delta) {
		var mc = Minecraft.getInstance();
		if (mc.options.hideGui) return;
		if (mc.gameMode != null && mc.gameMode.getPlayerMode() == GameType.SPECTATOR) return;
		if (!(mc.getCameraEntity() instanceof Player)) return;
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return;

		IQuickSwapToken<?> token = getDefaultWheelToken(player);
		if (token == null) return;

		int selected = token.getSelected();
		var list = token.getList();
		if (selected < 0 || selected >= list.size()) return;
		List<ItemStack> items = list.get(selected).asList();

		int w = g.guiWidth();
		int h = g.guiHeight();

		if (items.size() >= 4) {
			renderSetInSlot(g, items, w, h);
		} else {
			ItemStack stack = items.stream().filter(s -> !s.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
			if (stack.isEmpty()) return;
			renderSingleItem(g, stack, w, h);
		}
	}

	private static IQuickSwapToken<?> getDefaultWheelToken(LocalPlayer player) {
		var wheelTokens = QuickSwapManager.getWheelTokens(player, L2Keys.hasShiftDown());
		for (var t : wheelTokens) {
			if (t.type().supportWheel()) return t;
		}
		return null;
	}

	private static void renderSetInSlot(GuiGraphics g, List<ItemStack> items, int w, int h) {
		int size = 18;
		int x = w / 2 - 91 - 29 - size - 3;
		int y = h - 22 + 1;

		RenderSystem.enableBlend();
		g.blitSprite(SLOT, x - 2, y - 2, 29, 24);
		RenderSystem.disableBlend();

		Font font = Minecraft.getInstance().font;
		g.pose().pushPose();
		g.pose().translate(x + 1, y + 2, 0);
		g.pose().scale(0.5f, 0.5f, 1.0f);
		for (int i = 0; i < 4 && i < items.size(); i++) {
			ItemStack stack = items.get(i);
			if (stack.isEmpty()) continue;
			int ix = (i % 2) * 16;
			int iy = (i / 2) * 16;
			g.renderItem(stack, ix, iy);
			g.renderItemDecorations(font, stack, ix, iy);
		}
		g.pose().popPose();
	}

	private static void renderSingleItem(GuiGraphics g, ItemStack stack, int w, int h) {
		int size = 18;
		int x = w / 2 - 91 - 29 - size - 3;
		int y = h - 22 + 1;

		RenderSystem.enableBlend();
		g.blitSprite(SLOT, x - 2, y - 2, 29, 24);
		RenderSystem.disableBlend();
		g.renderItem(stack, x + 1, y + 2);
		g.renderItemDecorations(Minecraft.getInstance().font, stack, x + 1, y + 2);
	}

}
