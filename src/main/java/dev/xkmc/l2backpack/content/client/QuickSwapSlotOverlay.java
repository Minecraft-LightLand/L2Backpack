package dev.xkmc.l2backpack.content.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapManager;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2itemselector.init.data.L2ISConfig;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.List;

public class QuickSwapSlotOverlay implements IGuiOverlay {

	public static final QuickSwapSlotOverlay INSTANCE = new QuickSwapSlotOverlay();
	private static final ResourceLocation SLOT = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_left");


	@Override
	public void render(ForgeGui gui, GuiGraphics g, float delta, int sw, int sh) {
		if (!L2ISConfig.CLIENT.useFastSwitchWheel.get()) return;
		var mc = Minecraft.getInstance();
		if (mc.options.hideGui) return;
		if (mc.gameMode != null && mc.gameMode.getPlayerMode() == GameType.SPECTATOR) return;
		if (!(mc.getCameraEntity() instanceof Player)) return;
		LocalPlayer player = mc.player;
		if (player == null) return;

		ItemStack mainHand = player.getMainHandItem();
		boolean hasBow = mainHand.getItem() instanceof ProjectileWeaponItem;

		ItemStack arrow = ItemStack.EMPTY;
		if (hasBow) {
			arrow = getDisplayArrow(player, mainHand);
		}

		IQuickSwapToken<?> token = getDefaultWheelToken(player, hasBow);
		if (arrow.isEmpty() && token == null) return;

		int w = g.guiWidth();
		int h = g.guiHeight();
		int baseX = w / 2 - 91 - 29 - 18 - 3;
		int y = h - 22 + 1;

		int slotCount = 0;
		if (!arrow.isEmpty()) slotCount++;
		if (token != null) slotCount++;

		int startX = baseX - (slotCount - 1) * 23;

		if (!arrow.isEmpty()) {
			renderItemSlot(g, arrow, startX, y);
			startX += 23;
		}
		if (token != null) {
			int selected = token.getSelected();
			var list = token.getList();
			if (selected >= 0 && selected < list.size()) {
				List<ItemStack> items = list.get(selected).asList();
				if (items.size() >= 4) {
					renderSetInSlot(g, items, startX, y);
				} else {
					ItemStack stack = items.stream().filter(s -> !s.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
					renderItemSlot(g, stack, startX, y);
				}
			}
		}
	}

	private static ItemStack getDisplayArrow(LocalPlayer player, ItemStack bow) {
		var token = QuickSwapManager.getToken(player, bow, false);
		if (token != null && token.type() == QuickSwapTypes.ARROW) {
			int sel = token.getSelected();
			var list = token.getList();
			if (sel >= 0 && sel < list.size()) {
				ItemStack stack = list.get(sel).getStack();
				if (!stack.isEmpty()) return stack;
			}
		}
		return player.getProjectile(bow);
	}

	private static IQuickSwapToken<?> getDefaultWheelToken(LocalPlayer player, boolean hasBow) {
		var wheelTokens = QuickSwapManager.getWheelTokens(player, L2Keys.hasShiftDown());
		IQuickSwapToken<?> fallback = null;
		int count = 0;
		for (var t : wheelTokens) {
			if (!t.type().supportWheel()) continue;
			count++;
			if (!hasBow) return t;
			if (t.type() != QuickSwapTypes.ARROW) return t;
			fallback = t;
		}
		if (hasBow && count <= 1 && fallback != null) return null;
		return fallback;
	}

	private static void renderItemSlot(GuiGraphics g, ItemStack stack, int x, int y) {
		RenderSystem.enableBlend();
		g.blit(SLOT, x - 2, y - 2, 0, 0, 29, 24, 29, 24);
		RenderSystem.disableBlend();
		if (!stack.isEmpty()) {
			g.renderItem(stack, x + 1, y + 2);
			g.renderItemDecorations(Minecraft.getInstance().font, stack, x + 1, y + 2);
		}
	}

	private static void renderSetInSlot(GuiGraphics g, List<ItemStack> items, int x, int y) {
		RenderSystem.enableBlend();
		g.blit(SLOT, x - 2, y - 2, 0, 0, 29, 24, 29, 24);
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

}
