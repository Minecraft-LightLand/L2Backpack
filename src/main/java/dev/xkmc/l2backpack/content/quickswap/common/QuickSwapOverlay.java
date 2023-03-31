package dev.xkmc.l2backpack.content.quickswap.common;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
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

	public QuickSwapOverlay() {
		super(40, 3);
	}

	public boolean isScreenOn() {
		if (Minecraft.getInstance().screen != null) return false;
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return false;
		QuickSwapType type = QuickSwapManager.getValidType(player, Screen.hasAltDown());
		if (!Minecraft.getInstance().options.keyShift.isDown()) {
			if (type == QuickSwapType.ARROW && BackpackConfig.CLIENT.showArrowOnlyWithShift.get()) return false;
			if (type == QuickSwapType.TOOL && BackpackConfig.CLIENT.showToolOnlyWithShift.get()) return false;
			if (type == QuickSwapType.ARMOR && BackpackConfig.CLIENT.showArmorOnlyWithShift.get()) return false;
		}
		IQuickSwapToken token = QuickSwapManager.getToken(player, Screen.hasAltDown());
		return token != null;
	}

	@Override
	public Pair<List<ItemStack>, Integer> getItems() {
		LocalPlayer player = Proxy.getClientPlayer();
		IQuickSwapToken token = QuickSwapManager.getToken(player, Screen.hasAltDown());
		assert token != null;
		List<ItemStack> list = token.getList();
		int selected = token.getSelected();
		return Pair.of(list, selected);
	}

	@Override
	public int getSignature() {
		LocalPlayer player = Proxy.getClientPlayer();
		IQuickSwapToken token = QuickSwapManager.getToken(player, Screen.hasAltDown());
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
		QuickSwapType type = QuickSwapManager.getValidType(player, Screen.hasAltDown());
		if (type == QuickSwapType.ARROW) {
			ItemStack bowStack = player.getMainHandItem();
			if (bowStack.getItem() instanceof ProjectileWeaponItem bow) {
				return !stack.isEmpty() && bow.getAllSupportedProjectiles().test(stack);
			}
			return false;
		}
		if (type == QuickSwapType.ARMOR) {
			if (stack.isEmpty()) {
				return false;
			}
			EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(stack);
			if (player.getItemBySlot(slot).getItem() instanceof BaseBagItem) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean onCenter() {
		return BackpackConfig.CLIENT.previewOnCenter.get();
	}

	@Override
	public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height) {
		super.render(gui, poseStack, partialTick, width, height);
		LocalPlayer player = Proxy.getClientPlayer();
		if (QuickSwapManager.getValidType(player, Screen.hasAltDown()) == QuickSwapType.ARMOR && ease_time == max_ease) {
			int x = getXOffset(width);
			int y = 45 + getYOffset(height);
			if (onCenter()) {
				x -= 18;
			} else x += 18;
			ItemRenderer renderer = gui.getMinecraft().getItemRenderer();
			var pair = getItems();
			ItemStack hover = pair.getFirst().get(pair.getSecond());
			EquipmentSlot target = LivingEntity.getEquipmentSlotForItem(hover);
			for (int i = 0; i < 4; i++) {
				EquipmentSlot slot = EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, 3 - i);
				ItemStack stack = player.getItemBySlot(slot);
				Font font = gui.getMinecraft().font;
				ItemStack targetStack = player.getItemBySlot(target);
				renderArmorSlot(x, y, 64, target == slot, targetStack.getItem() instanceof BaseBagItem);
				if (!stack.isEmpty()) {
					renderer.renderAndDecorateItem(poseStack, stack, x, y);
					renderer.renderGuiItemDecorations(poseStack, font, stack, x, y);
				}
				y += 18;
			}
		}
	}

	public void renderArmorSlot(int x, int y, int a, boolean target, boolean invalid) {
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		Tesselator tex = Tesselator.getInstance();
		BufferBuilder builder = tex.getBuilder();
		OverlayUtils.fillRect(builder, x, y, 16, 16, 255, 255, 255, a);
		if (target) {
			if (invalid) {
				OverlayUtils.drawRect(builder, x, y, 16, 16, 220, 70, 70, 255);
			} else {
				OverlayUtils.drawRect(builder, x, y, 16, 16, 70, 150, 185, 255);
			}
		}
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
