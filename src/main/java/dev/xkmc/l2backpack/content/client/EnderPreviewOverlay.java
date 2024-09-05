package dev.xkmc.l2backpack.content.client;

import dev.xkmc.l2backpack.content.capability.PickupMode;
import dev.xkmc.l2backpack.content.drawer.DrawerBlockEntity;
import dev.xkmc.l2backpack.content.remote.dimensional.DimensionalBlockEntity;
import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerBlockEntity;
import dev.xkmc.l2backpack.events.TooltipUpdateEvents;
import dev.xkmc.l2backpack.init.data.LBLang;
import dev.xkmc.l2core.util.Proxy;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

public class EnderPreviewOverlay implements LayeredDraw.Layer {

	@Override
	public void render(GuiGraphics g, DeltaTracker delta) {
		LocalPlayer player = Proxy.getClientPlayer();
		var ray = Minecraft.getInstance().hitResult;
		if (!(ray instanceof BlockHitResult bray)) return;
		BlockPos pos = bray.getBlockPos();
		BlockEntity entity = player.level().getBlockEntity(pos);
		int count = 0;
		Item item = Items.AIR;
		if (entity instanceof EnderDrawerBlockEntity drawer) {
			count = TooltipUpdateEvents.getCount(drawer.ownerId, drawer.item);
			item = drawer.getItem().getItem();
		} else if (entity instanceof DrawerBlockEntity drawer) {
			count = drawer.handler.count;
			item = drawer.getItem().getItem();
		}
		Font font = Minecraft.getInstance().font;
		if (item != Items.AIR) {
			Component text = LBLang.IDS.DRAWER_CONTENT.get(item.getDescription(), count < 0 ? "???" : count);
			renderText(font, g, g.guiWidth() / 2, g.guiHeight() / 2 + 16, text);
		}
		if (entity instanceof DimensionalBlockEntity be) {
			if (be.config != null && be.config.pickup() != PickupMode.NONE) {
				int off = font.lineHeight;
				renderText(font, g, g.guiWidth() / 2, g.guiHeight() / 2 + 16, be.config.pickup().getTooltip());
				renderText(font, g, g.guiWidth() / 2, g.guiHeight() / 2 + 16 + off, be.config.destroy().getTooltip());
			}
		}
	}

	private static void renderText(Font font, GuiGraphics g, int x, int y, Component text) {
		x -= font.width(text) / 2;
		g.drawString(font, text, x, y, 0xFFFFFFFF);
	}

}
