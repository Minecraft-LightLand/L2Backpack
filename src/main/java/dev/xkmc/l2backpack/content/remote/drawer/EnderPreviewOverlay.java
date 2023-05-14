package dev.xkmc.l2backpack.content.remote.drawer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2backpack.content.drawer.DrawerBlockEntity;
import dev.xkmc.l2backpack.events.TooltipUpdateEvents;
import dev.xkmc.l2backpack.init.data.LangData;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2library.util.raytrace.RayTraceUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class EnderPreviewOverlay implements IGuiOverlay {

	@Override
	public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
		LocalPlayer player = Proxy.getClientPlayer();
		var ray = RayTraceUtil.rayTraceBlock(player.level, player, player.getReachDistance());
		if (ray.getType() == HitResult.Type.BLOCK) {
			BlockPos pos = ray.getBlockPos();
			BlockEntity entity = player.level.getBlockEntity(pos);
			int count = 0;
			Item item = Items.AIR;
			if (entity instanceof EnderDrawerBlockEntity drawer) {
				count = TooltipUpdateEvents.getCount(drawer.owner_id, drawer.item);
				item = drawer.getItem();
			} else if (entity instanceof DrawerBlockEntity drawer) {
				count = drawer.handler.count;
				item = drawer.getItem();
			}
			if (item != Items.AIR) {
				gui.setupOverlayRenderState(true, false);
				Component text = LangData.IDS.DRAWER_CONTENT.get(item.getDescription(), count < 0 ? "???" : count);
				renderText(gui, poseStack, screenWidth / 2f, screenHeight / 2f + 16, text);
			}
		}
	}

	private static void renderText(ForgeGui gui, PoseStack stack, float x, float y, Component text) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		Minecraft mc = gui.getMinecraft();
		Font font = gui.getFont();
		x -= font.width(text) / 2f;
		font.drawShadow(stack, text, x, y, 0xFFFFFFFF);
		RenderSystem.disableBlend();
	}

}
