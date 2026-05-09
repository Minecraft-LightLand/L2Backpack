package dev.xkmc.l2backpack.content.quickswap.common;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.content.quickswap.entry.ISwapEntry;
import dev.xkmc.l2backpack.content.quickswap.type.ISideInfoRenderer;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapManager;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import dev.xkmc.l2backpack.events.BackpackSel;
import dev.xkmc.l2backpack.init.data.LBConfig;
import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.overlay.SelectionSideBar;
import dev.xkmc.l2itemselector.overlay.SideBar;
import dev.xkmc.l2serial.util.Wrappers;
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

		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (!(obj instanceof BackpackSignature sig)) return false;
			return backpackSelect == sig.backpackSelect &&
					ignoreOther == sig.ignoreOther &&
					type == sig.type &&
					playerSelect == sig.playerSelect &&
					ItemStack.isSameItemSameComponents(stack, sig.stack);
		}
	}

	public static QuickSwapOverlay INSTANCE = new QuickSwapOverlay();

	public QuickSwapOverlay() {
		super(40, 3);
	}

	public boolean isScreenOn() {
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return false;
		if (L2Keys.WHEEL.map.isDown()) return false;
		return BackpackSel.INSTANCE.isClientActive(player);
	}

	public static boolean hasShiftDown() {
		return L2Keys.hasShiftDown();
	}

	public static boolean hasAltDown() {
		return L2Keys.hasAltDown();
	}

	@Override
	public boolean isOnHold() {
		return hasShiftDown() || hasAltDown() || L2Keys.SWAP.map.isDown();
	}

	@Override
	public Pair<List<ISwapEntry<?>>, Integer> getItems() {
		LocalPlayer player = Proxy.getClientPlayer();
		assert player != null;
		IQuickSwapToken<?> token = QuickSwapManager.getToken(player, hasAltDown());
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
		IQuickSwapToken<?> token = QuickSwapManager.getToken(player, hasAltDown());
		assert token != null;
		int selected = token.getSelected();
		boolean ignoreOther = false;
		QuickSwapType type = token.type();
		if (!isOnHold()) {
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
		return LBConfig.CLIENT.previewOnCenter.get();
	}

	protected void renderEntry(SelectionSideBar.Context ctx, ISwapEntry<?> token, int i, int selected) {
		LocalPlayer player = Proxy.getClientPlayer();
		assert player != null;
		QuickSwapType type = token.token().type();
		float progress = (max_ease - ease_time) / max_ease;
		type.renderSelected(ctx, player, token, new EntryRenderContext(i, progress, ctx.x0(), 18 * i + ctx.y0(),
				selected == i && this.ease_time == this.max_ease, onCenter()));
	}

	@Override
	public void renderContent(Context ctx) {
		super.renderContent(ctx);
		LocalPlayer player = Proxy.getClientPlayer();
		assert player != null;
		var pair = getItems();
		var hover = pair.getFirst().get(pair.getSecond());
		var type = hover.token().type();
		if (ease_time == max_ease && type instanceof ISideInfoRenderer rtype) {
			int x = ctx.x0();
			int y = 81 + ctx.y0();
			if (onCenter()) {
				x -= 18;
			} else x += 18 * hover.asList().size();
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
