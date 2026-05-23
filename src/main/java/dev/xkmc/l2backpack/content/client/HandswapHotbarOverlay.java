package dev.xkmc.l2backpack.content.client;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.handswap.HandswapItem;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2core.util.Proxy;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import java.util.List;

public class HandswapHotbarOverlay implements LayeredDraw.Layer {

	public static final HandswapHotbarOverlay INSTANCE = new HandswapHotbarOverlay();

	@Override
	public void render(GuiGraphics g, DeltaTracker delta) {
		var mc = Minecraft.getInstance();
		if (mc.options.hideGui) return;
		if (mc.gameMode != null && mc.gameMode.getPlayerMode() == GameType.SPECTATOR) return;
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return;

		ItemStack bag = findBag(player);
		if (bag.isEmpty()) return;
		int selected = player.getInventory().selected;
		List<ItemStack> items = BaseBagItem.getItems(bag);
		if (items.size() < 9) return;

		int w = g.guiWidth();
		int h = g.guiHeight();

		for (int i = 0; i < 9; i++) {
			if (i == selected) continue;
			ItemStack stack = items.get(i);
			if (stack.isEmpty()) continue;
			int sx = w / 2 - 90 + i * 20 + 1;
			int sy = h - 22 + 2;
			g.pose().pushPose();
			g.pose().translate(sx, sy, 0);
			g.pose().scale(0.5f, 0.5f, 1.0f);
			g.renderItem(stack, 0, 0);
			g.pose().popPose();
		}

		int ox = w / 2 - 91 - 29;
		int oy = h - 22 + 2;
		ItemStack offhandItem = items.get(selected);
		if (!offhandItem.isEmpty()) {
			g.pose().pushPose();
			g.pose().translate(ox, oy, 0);
			g.pose().scale(0.5f, 0.5f, 1.0f);
			g.renderItem(offhandItem, 0, 0);
			g.pose().popPose();
		}
	}

	private static ItemStack findBag(LocalPlayer player) {
		ItemStack bag = HandswapItem.getToken(player);
		if (!bag.isEmpty()) return bag;
		var inv = player.getEnderChestInventory();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			var stack = inv.getItem(i);
			if (stack.getItem() instanceof HandswapItem) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

}
