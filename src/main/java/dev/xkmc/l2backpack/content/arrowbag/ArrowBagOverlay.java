package dev.xkmc.l2backpack.content.arrowbag;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.List;

public class ArrowBagOverlay implements IGuiOverlay {

	public static boolean isScreenOn() {
		if (Minecraft.getInstance().screen != null) return false;
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return false;
		if (!(player.getMainHandItem().getItem() instanceof ProjectileWeaponItem weapon)) return false;
		ItemStack bag = ArrowBagManager.getArrowBag(player);
		if (bag.isEmpty()) return false;
		List<ItemStack> list = ArrowBag.getItems(bag);
		if (list.isEmpty()) return false;
		for (ItemStack stack : list) {
			if (!stack.isEmpty() && weapon.getAllSupportedProjectiles().test(stack)) return true;
		}
		return false;
	}

	@Override
	public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height) {
		if (!isScreenOn()) return;
		gui.setupOverlayRenderState(true, false);
		LocalPlayer player = Proxy.getClientPlayer();
		ProjectileWeaponItem weapon = (ProjectileWeaponItem) player.getMainHandItem().getItem();
		ItemStack bag = ArrowBagManager.getArrowBag(player);
		List<ItemStack> list = ArrowBag.getItems(bag);
		ItemRenderer renderer = gui.getMinecraft().getItemRenderer();
		Font font = gui.getMinecraft().font;
		int selected = ArrowBag.getSelected(bag);
		ItemStack used = player.getProjectile(player.getMainHandItem());
		for (int i = 0; i < list.size(); i++) {
			ItemStack stack = list.get(i);
			int x = width / 2 + 18 * 3 + 1;
			int y = height / 2 - 81 + 18 * i + 1;
			if (selected == i) {
				boolean shift = Minecraft.getInstance().options.keyShift.isDown();
				boolean match = !used.isEmpty() && ItemStack.isSameItemSameTags(stack, used) && stack.getCount() == used.getCount();
				renderCooldown(x, y, shift ? 127 : 64, match);
			}
			if (!stack.isEmpty()) {
				renderer.renderAndDecorateItem(stack, x, y);
				renderer.renderGuiItemDecorations(font, stack, x, y);
			}
		}
	}

	private static void renderCooldown(int x, int y, int a, boolean selected) {
		RenderSystem.disableDepthTest();
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		Tesselator tex = Tesselator.getInstance();
		BufferBuilder builder = tex.getBuilder();
		fillRect(builder, x, y, 16, 16, 255, 255, 255, a);
		if (selected)
			drawRect(builder, x, y, 16, 16, 0xff, 0xaa, 0, 255);
		RenderSystem.enableTexture();
		RenderSystem.enableDepthTest();
	}

	private static void drawRect(BufferBuilder builder, int x, int y, int w, int h, int r, int g, int b, int a) {
		fillRect(builder, x - 1, y - 1, w + 2, 1, r, g, b, a);
		fillRect(builder, x - 1, y - 1, 1, h + 2, r, g, b, a);
		fillRect(builder, x - 1, y + h, w + 2, 1, r, g, b, a);
		fillRect(builder, x + w, y - 1, 1, h + 2, r, g, b, a);
	}

	private static void fillRect(BufferBuilder builder, int x, int y, int w, int h, int r, int g, int b, int a) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		builder.vertex(x, y, 0.0D).color(r, g, b, a).endVertex();
		builder.vertex(x, y + h, 0.0D).color(r, g, b, a).endVertex();
		builder.vertex(x + w, y + h, 0.0D).color(r, g, b, a).endVertex();
		builder.vertex(x + w, y, 0.0D).color(r, g, b, a).endVertex();
		BufferUploader.drawWithShader(builder.end());
	}

}
