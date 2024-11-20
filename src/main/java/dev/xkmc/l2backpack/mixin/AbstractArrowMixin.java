package dev.xkmc.l2backpack.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.xkmc.l2backpack.events.CapabilityEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractArrow.class)
public class AbstractArrowMixin {

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z"), method = "tryPickup")
	public boolean l2backpack$playerTouch$tryArrowPickup(Inventory inv, ItemStack stack, Operation<Boolean> op) {
		if (stack.isEmpty()) return op.call(inv, stack);
		int old = stack.getCount();
		CapabilityEvents.touchArrow(stack, inv);
		if (stack.isEmpty()) {
			return true;
		}
		boolean success = stack.getCount() < old;
		boolean ans = op.call(inv, stack);
		return ans || success;
	}

}
