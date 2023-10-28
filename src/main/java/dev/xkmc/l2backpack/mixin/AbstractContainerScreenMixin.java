package dev.xkmc.l2backpack.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.xkmc.l2backpack.content.capability.PickupBagItem;
import dev.xkmc.l2backpack.content.tool.IBagTool;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

	@Shadow
	@Nullable
	protected Slot hoveredSlot;

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"), method = "renderTooltip")
	public boolean l2backpack$renderTooltips$isEmpty(ItemStack stack, Operation<Boolean> bool) {
		if (stack.getItem() instanceof IBagTool tool) {
			if (hoveredSlot != null && hoveredSlot.getItem().getItem() instanceof PickupBagItem) {
				return true;
			}
		}
		return bool.call(stack);
	}

}
