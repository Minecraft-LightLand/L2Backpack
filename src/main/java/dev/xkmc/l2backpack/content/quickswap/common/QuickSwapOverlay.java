package dev.xkmc.l2backpack.content.quickswap.common;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.init.data.BackpackConfig;
import dev.xkmc.l2library.base.overlay.SelectionSideBar;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

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
		if (token == null) return false;
		return token.isTokenValid(player);
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
		return focus * 10 + selected;
	}

	@Override
	public boolean isAvailable(ItemStack stack) {
		return !used.isEmpty() && ItemStack.isSameItemSameTags(stack, used) && stack.getCount() == used.getCount();
	}

	@Override
	public boolean onCenter() {
		return BackpackConfig.CLIENT.previewOnCenter.get();
	}

	@Override
	public void initRender() {
		LocalPlayer player = Proxy.getClientPlayer();
		this.used = player.getProjectile(player.getMainHandItem());
	}

	@Override
	protected int getXOffset(int width) {
		float progress = (max_ease - ease_time) / max_ease;
		if (onCenter())
			return width / 2 + 18 * 3 + 1 + Math.round(progress * width / 2);
		else
			return width - 18 + Math.round(progress * 20);
	}

	@Override
	protected int getYOffset(int height) {
		return height / 2 - 81 + 1;
	}
}
