package dev.xkmc.l2backpack.content.drawer;

import dev.xkmc.l2backpack.content.capability.BackpackCap;
import dev.xkmc.l2backpack.content.capability.InvBackpackCap;
import dev.xkmc.l2backpack.content.capability.PickupMode;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class DrawerInvWrapper extends InvBackpackCap implements ICapabilityProvider {

	private final ItemStack stack;
	private final Function<PickupTrace, BaseDrawerInvAccess> access;
	private final LazyOptional<DrawerInvWrapper> holder = LazyOptional.of(() -> this);

	public DrawerInvWrapper(ItemStack stack, Function<PickupTrace, BaseDrawerInvAccess> access) {
		this.stack = stack;
		this.access = access;
	}

	@Override
	public @Nullable IItemHandlerModifiable getInv(PickupTrace trace) {
		return access.apply(trace);
	}

	@Override
	@NotNull
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == BackpackCap.TOKEN) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public PickupMode getMode() {
		return BackpackCap.getMode(stack);
	}

	@Override
	public int getSignature() {
		return stack.hashCode();
	}

}