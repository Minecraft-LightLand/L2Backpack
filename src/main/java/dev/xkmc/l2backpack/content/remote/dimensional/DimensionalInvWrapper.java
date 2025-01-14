package dev.xkmc.l2backpack.content.remote.dimensional;

import dev.xkmc.l2backpack.init.registrate.LBTriggers;
import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.l2core.util.ServerProxy;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DimensionalInvWrapper extends InvWrapper {

	private final UUID id;

	public DimensionalInvWrapper(Container inv, UUID id) {
		super(inv);
		this.id = id;
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		ItemStack ans = super.insertItem(slot, stack, simulate);
		if (stack.getCount() != ans.getCount() && !simulate) {
			ServerProxy.getServer().map(e -> e.getPlayerList().getPlayer(id)).ifPresent(LBTriggers.REMOTE.get()::trigger);
		}
		return ans;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack ans = super.extractItem(slot, amount, simulate);
		if (!ans.isEmpty() && !simulate) {
			ServerProxy.getServer().map(e -> e.getPlayerList().getPlayer(id)).ifPresent(LBTriggers.REMOTE.get()::trigger);
		}
		return ans;
	}

}
