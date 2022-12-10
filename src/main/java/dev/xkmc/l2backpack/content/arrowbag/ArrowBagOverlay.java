package dev.xkmc.l2backpack.content.arrowbag;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.init.data.BackpackConfig;
import dev.xkmc.l2library.base.overlay.SelectionSideBar;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import java.util.List;

public class ArrowBagOverlay extends SelectionSideBar {

	public static ArrowBagOverlay INSTANCE = new ArrowBagOverlay();

	private ItemStack used = ItemStack.EMPTY;

	public ArrowBagOverlay() {
		super(40, 3);
	}

	public boolean isScreenOn() {
		if (Minecraft.getInstance().screen != null) return false;
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return false;
		if (!(player.getMainHandItem().getItem() instanceof ProjectileWeaponItem weapon)) return false;
		ItemStack bag = ArrowBagManager.getArrowBag(player);
		if (bag.isEmpty()) return false;
		List<ItemStack> list = BaseBagItem.getItems(bag);
		if (list.isEmpty()) return false;
		for (ItemStack stack : list) {
			if (!stack.isEmpty() && weapon.getAllSupportedProjectiles().test(stack)) return true;
		}
		return false;
	}

	@Override
	public Pair<List<ItemStack>, Integer> getItems() {
		LocalPlayer player = Proxy.getClientPlayer();
		ItemStack bag = ArrowBagManager.getArrowBag(player);
		List<ItemStack> list = BaseBagItem.getItems(bag);
		int selected = ArrowBag.getSelected(bag);
		return Pair.of(list, selected);
	}

	@Override
	public int getSignature() {
		LocalPlayer player = Proxy.getClientPlayer();
		ItemStack bag = ArrowBagManager.getArrowBag(player);
		int selected = ArrowBag.getSelected(bag);
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
