package dev.xkmc.l2backpack.content.quickswap.type;

import dev.xkmc.l2backpack.content.quickswap.entry.ISwapEntry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class QuickSwapType {

	private final String name;
	private final int index;

	QuickSwapType(String name, int index) {
		this.name = name;
		this.index = index;
	}

	public QuickSwapType(String name) {
		this.name = name;
		index = QuickSwapTypes.register(this);
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public abstract boolean activePopup();

	public abstract ItemStack getSignatureItem(Player player);

	public boolean isAvailable(Player player, ISwapEntry<?> token) {
		return true;
	}

	public boolean isAvailable(Player player, ISwapEntry<?> token, int index) {
		return isAvailable(player, token);
	}

	public static int color(int r, int g, int b, int a) {
		return a << 24 | r << 16 | g << 8 | b;
	}

	public boolean supportWheel() {
		return true;
	}

}
