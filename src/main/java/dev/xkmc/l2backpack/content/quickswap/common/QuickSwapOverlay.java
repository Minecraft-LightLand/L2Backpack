package dev.xkmc.l2backpack.content.quickswap.common;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.events.BackpackSel;
import dev.xkmc.l2backpack.init.data.BackpackConfig;
import dev.xkmc.l2library.base.overlay.ItemSelSideBar;
import dev.xkmc.l2library.base.overlay.SideBar;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuickSwapOverlay extends ItemSelSideBar<QuickSwapOverlay.BackpackSignature> {

	public record BackpackSignature(int backpackSelect, boolean ignoreOther, QuickSwapType type,
									int playerSelect, ItemStack stack)
			implements Signature<BackpackSignature> {

		@Override
		public boolean shouldRefreshIdle(SideBar<?> sideBar, @Nullable QuickSwapOverlay.BackpackSignature old) {
			if (ignoreOther) {
				if (old == null) return false;
				if (old.type != type()) return true;
				return old.backpackSelect != backpackSelect();
			}
			return !equals(old);
		}
	}

	public static QuickSwapOverlay INSTANCE = new QuickSwapOverlay();

	public QuickSwapOverlay() {
		super(40, 3);
	}

	public boolean isScreenOn() {
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return false;
		return BackpackSel.INSTANCE.isClientActive(player);
	}

	@Override
	protected boolean isOnHold() {
		return super.isOnHold() || Screen.hasShiftDown() || Screen.hasAltDown();
	}

	@Override
	public Pair<List<ItemStack>, Integer> getItems() {
		LocalPlayer player = Proxy.getClientPlayer();
		assert player != null;
		IQuickSwapToken token = QuickSwapManager.getToken(player, Screen.hasAltDown());
		assert token != null;
		List<ItemStack> list = token.getList();
		int selected = token.getSelected();
		return Pair.of(list, selected);
	}

	@Override
	public BackpackSignature getSignature() {
		LocalPlayer player = Proxy.getClientPlayer();
		assert player != null;
		IQuickSwapToken token = QuickSwapManager.getToken(player, Screen.hasAltDown());
		assert token != null;
		int selected = token.getSelected();
		boolean ignoreOther = false;
		QuickSwapType type = QuickSwapManager.getValidType(player, Screen.hasAltDown());
		if (!Minecraft.getInstance().options.keyShift.isDown()) {
			ignoreOther = type == QuickSwapType.ARROW && BackpackConfig.CLIENT.showArrowOnlyWithShift.get()
					|| type == QuickSwapType.TOOL && BackpackConfig.CLIENT.showToolOnlyWithShift.get()
					|| type == QuickSwapType.ARMOR && BackpackConfig.CLIENT.showArmorOnlyWithShift.get();
		}
		int focus = player.getInventory().selected;
		if (token.type() == QuickSwapType.TOOL) {
			return new BackpackSignature(selected, ignoreOther, type, focus, player.getMainHandItem());
		} else {
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if (slot.getType() != EquipmentSlot.Type.ARMOR)
					continue;
				return new BackpackSignature(selected, ignoreOther, type, focus, player.getItemBySlot(slot));
			}
		}
		return new BackpackSignature(selected, ignoreOther, type, focus, ItemStack.EMPTY);
	}

	@Override
	public boolean isAvailable(ItemStack stack) {
		LocalPlayer player = Proxy.getClientPlayer();
		assert player != null;
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
			return !(player.getItemBySlot(slot).getItem() instanceof BaseBagItem);
		}
		return true;
	}

	@Override
	public boolean onCenter() {
		return BackpackConfig.CLIENT.previewOnCenter.get();
	}

	@Override
	public void renderContent(Context ctx) {
		super.renderContent(ctx);
		LocalPlayer player = Proxy.getClientPlayer();
		assert player != null;
		if (QuickSwapManager.getValidType(player, Screen.hasAltDown()) == QuickSwapType.ARMOR && ease_time == max_ease) {
			int x = ctx.x0();
			int y = 45 + ctx.y0();
			if (onCenter()) {
				x -= 18;
			} else x += 18;
			var pair = getItems();
			ItemStack hover = pair.getFirst().get(pair.getSecond());
			EquipmentSlot target = LivingEntity.getEquipmentSlotForItem(hover);
			for (int i = 0; i < 4; i++) {
				EquipmentSlot slot = EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, 3 - i);
				ItemStack stack = player.getItemBySlot(slot);
				ItemStack targetStack = player.getItemBySlot(target);
				renderArmorSlot(ctx.g(), x, y, 64, target == slot, targetStack.getItem() instanceof BaseBagItem);
				ctx.renderItem(stack, x, y);
				y += 18;
			}
		}
	}

	public void renderArmorSlot(GuiGraphics g, int x, int y, int a, boolean target, boolean invalid) {
		g.fill(x, y, 16, 16, color(255, 255, 255, a));
		if (target) {
			if (invalid) {
				g.fill(x, y, 16, 16, color(220, 70, 70, 255));
			} else {
				g.fill(x, y, 16, 16, color(70, 150, 185, 255));
			}
		}
	}

	@Override
	protected int getXOffset(int width) {
		float progress = (max_ease - ease_time) / max_ease;
		if (onCenter())
			return Math.round(width / 2f + 18 * 3 + 1 + progress * width / 2);
		else
			return Math.round(width - 36 + progress * 20);
	}

	@Override
	protected int getYOffset(int height) {
		return height / 2 - 81 + 1;
	}

}
