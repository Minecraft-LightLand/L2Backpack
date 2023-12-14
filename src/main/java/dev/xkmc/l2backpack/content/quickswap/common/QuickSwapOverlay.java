package dev.xkmc.l2backpack.content.quickswap.common;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.content.quickswap.entry.ISwapEntry;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapManager;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import dev.xkmc.l2backpack.content.quickswap.type.ISideInfoRenderer;
import dev.xkmc.l2backpack.events.BackpackSel;
import dev.xkmc.l2backpack.init.data.BackpackConfig;
import dev.xkmc.l2library.base.overlay.SelectionSideBar;
import dev.xkmc.l2library.base.overlay.SideBar;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuickSwapOverlay extends SelectionSideBar<ISwapEntry<?>, QuickSwapOverlay.BackpackSignature> {

	public record BackpackSignature(int backpackSelect, boolean ignoreOther, @Nullable QuickSwapType type,
									int playerSelect, ItemStack stack)
			implements Signature<BackpackSignature> {

		@Override
		public boolean shouldRefreshIdle(SideBar<?> sideBar, @Nullable QuickSwapOverlay.BackpackSignature old) {
			if (ignoreOther) {
				if (old == null) return false;
				return old.type == type && old.backpackSelect != backpackSelect();
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
		return super.isOnHold() || Screen.hasAltDown();
	}

	@Override
	public Pair<List<ISwapEntry<?>>, Integer> getItems() {
		LocalPlayer player = Proxy.getClientPlayer();
		assert player != null;
		IQuickSwapToken<?> token = QuickSwapManager.getToken(player, Screen.hasAltDown());
		assert token != null;
		List<? extends ISwapEntry<?>> list = token.getList();
		int selected = token.getSelected();
		return Pair.of(Wrappers.cast(list), selected);
	}

	public static boolean activePopup(@Nullable QuickSwapType type) {
		return type != null && type.activePopup();
	}

	@Override
	public BackpackSignature getSignature() {
		LocalPlayer player = Proxy.getClientPlayer();
		assert player != null;
		IQuickSwapToken<?> token = QuickSwapManager.getToken(player, Screen.hasAltDown());
		assert token != null;
		int selected = token.getSelected();
		boolean ignoreOther = false;
		QuickSwapType type = token.type();
		if (!Minecraft.getInstance().options.keyShift.isDown()) {
			ignoreOther = !activePopup(type);
		}
		int focus = player.getInventory().selected;
		ItemStack sel = type.getSignatureItem(player);
		return new BackpackSignature(selected, ignoreOther, type, focus, sel);
	}

	@Override
	public boolean isAvailable(ISwapEntry<?> token) {
		LocalPlayer player = Proxy.getClientPlayer();
		assert player != null;
		QuickSwapType type = token.token().type();
		return type.isAvailable(player, token);
	}

	@Override
	public boolean onCenter() {
		return BackpackConfig.CLIENT.previewOnCenter.get();
	}

	protected void renderEntry(SelectionSideBar.Context ctx, ISwapEntry<?> token, int i, int selected) {
		LocalPlayer player = Proxy.getClientPlayer();
		assert player != null;
		QuickSwapType type = token.token().type();
		type.renderSelected(ctx, player, token, ctx.x0(), 18 * i + ctx.y0(),
				selected == i && this.ease_time == this.max_ease, onCenter());
	}

	@Override
	public void renderContent(Context ctx) {
		super.renderContent(ctx);
		LocalPlayer player = Proxy.getClientPlayer();
		assert player != null;
		var type = QuickSwapManager.getValidType(player, Screen.hasAltDown());
		if (ease_time == max_ease && type instanceof ISideInfoRenderer rtype) {
			int x = ctx.x0();
			int y = 45 + ctx.y0();
			if (onCenter()) {
				x -= 18;
			} else x += 18;
			var pair = getItems();
			var hover = pair.getFirst().get(pair.getSecond());
			rtype.renderSide(ctx, x, y, player, hover);
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
