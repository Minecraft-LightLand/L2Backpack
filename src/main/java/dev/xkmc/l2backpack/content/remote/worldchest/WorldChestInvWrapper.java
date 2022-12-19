package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.init.advancement.BackpackTriggers;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WorldChestInvWrapper extends InvWrapper {

	private final UUID id;

	public WorldChestInvWrapper(Container inv, UUID id) {
		super(inv);
		this.id = id;
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		ItemStack ans = super.insertItem(slot, stack, simulate);
		if (stack.getCount() != ans.getCount() && !simulate) {
			Proxy.getServer().map(e -> e.getPlayerList().getPlayer(id)).ifPresent(BackpackTriggers.REMOTE::trigger);
		}
		return ans;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack ans = super.extractItem(slot, amount, simulate);
		if (!ans.isEmpty() && !simulate) {
			Proxy.getServer().map(e -> e.getPlayerList().getPlayer(id)).ifPresent(BackpackTriggers.REMOTE::trigger);
		}
		return ans;
	}

}
