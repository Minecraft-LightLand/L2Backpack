package dev.xkmc.l2backpack.content.quickswap.common;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.init.data.BackpackConfig;
import dev.xkmc.l2library.base.overlay.OverlayUtils;
import dev.xkmc.l2library.base.overlay.SelectionSideBar;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import java.util.List;

public class QuickSwapOverlay extends SelectionSideBar {

	public static QuickSwapOverlay INSTANCE = new QuickSwapOverlay();

	private ItemStack used = ItemStack.EMPTY;

	public QuickSwapOverlay() {
		super(40, 3);
	}

	public boolean isScreenOn() {
		if (Minecraft.getInstance().screen != null) return false;
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return false;
		IQuickSwapToken token = QuickSwapManager.getToken(player);
		return token != null;
	}

	@Override
	public Pair<List<ItemStack>, Integer> getItems() {
		LocalPlayer player = Proxy.getClientPlayer();
		IQuickSwapToken token = QuickSwapManager.getToken(player);
		assert token != null;
		List<ItemStack> list = token.getList();
		int selected = token.getSelected();
		return Pair.of(list, selected);
	}

	@Override
	public int getSignature() {
		LocalPlayer player = Proxy.getClientPlayer();
		IQuickSwapToken token = QuickSwapManager.getToken(player);
		assert token != null;
		int selected = token.getSelected();
		int focus = player.getInventory().selected;
		int ans = focus * 10 + selected;
		ans += token.type().ordinal() * 100;
		if (token.type() == QuickSwapType.ARROW)
			return ans;
		if (token.type() == QuickSwapType.TOOL) {
			ans += player.getMainHandItem().hashCode() & 0xFFFF;
		} else {
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if (slot.getType() != EquipmentSlot.Type.ARMOR)
					continue;
				ans += player.getItemBySlot(slot).hashCode() & 0xFFFF;
			}
		}
		return ans;
	}

	@Override
	public boolean isAvailable(ItemStack stack) {
		LocalPlayer player = Proxy.getClientPlayer();
		QuickSwapType type = QuickSwapManager.getValidType(player);
		if (type == QuickSwapType.ARROW) {
			ItemStack bowStack = player.getMainHandItem();
			if (bowStack.getItem() instanceof ProjectileWeaponItem bow) {
				return !stack.isEmpty() && bow.getAllSupportedProjectiles().test(stack);
			}
			return false;
		}
		if (type == QuickSwapType.ARMOR)
			return !stack.isEmpty();
		return true;
	}

	@Override
	public boolean onCenter() {
		return BackpackConfig.CLIENT.previewOnCenter.get();
	}

	@Override
	public void initRender() {
		LocalPlayer player = Proxy.getClientPlayer();
		QuickSwapType type = QuickSwapManager.getValidType(player);
		if (type == QuickSwapType.ARROW)
			this.used = player.getProjectile(player.getMainHandItem());
	}

	@Override
	public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height) {
		super.render(gui, poseStack, partialTick, width, height);
		LocalPlayer player = Proxy.getClientPlayer();
		if (QuickSwapManager.getValidType(player) == QuickSwapType.ARMOR && ease_time == max_ease) {
			int x = getXOffset(width);
			int y = 45 + getYOffset(height);
			if (onCenter()) {
				x -= 18;
			} else x += 18;
			boolean shift = Screen.hasShiftDown();
			ItemRenderer renderer = gui.getMinecraft().getItemRenderer();
			var pair = getItems();
			ItemStack hover = pair.getFirst().get(pair.getSecond());
			EquipmentSlot target = LivingEntity.getEquipmentSlotForItem(hover);
			for (int i = 0; i < 4; i++) {
				EquipmentSlot slot = EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, 3 - i);
				ItemStack stack = player.getItemBySlot(slot);
				Font font = gui.getMinecraft().font;
				renderArmorSlot(x, y, 64, target == slot);
				if (!stack.isEmpty()) {
					renderer.renderAndDecorateItem(stack, x, y);
					renderer.renderGuiItemDecorations(font, stack, x, y);
				}
				y += 18;
			}
		}
	}

	public void renderArmorSlot(int x, int y, int a, boolean target) {
		RenderSystem.disableDepthTest();
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		Tesselator tex = Tesselator.getInstance();
		BufferBuilder builder = tex.getBuilder();
		OverlayUtils.fillRect(builder, x, y, 16, 16, 255, 255, 255, a);
		if (target) {
			OverlayUtils.drawRect(builder, x, y, 16, 16, 70, 150, 185, 255);
		}
		RenderSystem.enableTexture();
		RenderSystem.enableDepthTest();
	}

	@Override
	protected int getXOffset(int width) {
		float progress = (max_ease - ease_time) / max_ease;
		if (onCenter())
			return width / 2 + 18 * 3 + 1 + Math.round(progress * width / 2);
		else
			return width - 36 + Math.round(progress * 20);
	}

	@Override
	protected int getYOffset(int height) {
		return height / 2 - 81 + 1;
	}

}
