package dev.xkmc.l2backpack.content.drawer;

import dev.xkmc.l2backpack.content.capability.InvPickupCap;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupModeCap;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class DrawerInvWrapper extends InvPickupCap<BaseDrawerInvAccess> implements ICapabilityProvider {

	private final ItemStack stack;
	private final Function<PickupTrace, BaseDrawerInvAccess> access;
	private final LazyOptional<DrawerInvWrapper> holder = LazyOptional.of(() -> this);

	public DrawerInvWrapper(ItemStack stack, Function<PickupTrace, BaseDrawerInvAccess> access) {
		this.stack = stack;
		this.access = access;
	}

	@Override
	public @Nullable BaseDrawerInvAccess getInv(PickupTrace trace) {
		return access.apply(trace);
	}

	@Override
	public boolean mayStack(BaseDrawerInvAccess inv, int slot, ItemStack stack, PickupConfig config) {
		return inv.mayStack(inv, slot, stack, config);
	}

	@Override
	@NotNull
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == PickupModeCap.TOKEN) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public PickupConfig getPickupMode() {
		return PickupConfig.getPickupMode(stack);
	}

	@Override
	public int getSignature() {
		return stack.hashCode();
	}

}