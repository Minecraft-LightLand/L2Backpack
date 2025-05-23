package dev.xkmc.l2backpack.mixin;

import dev.xkmc.l2backpack.events.ItemStackShrinkProvider;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.IntConsumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackShrinkProvider {

	@Unique
	private IntConsumer l2backpack_shrinkListener;

	@Override
	public void l2backpack$setShrinkListener(IntConsumer cons) {
		l2backpack_shrinkListener = cons;
	}

	@Inject(at = @At("HEAD"), method = "shrink")
	public void l2backpack_shrinkListener(int count, CallbackInfo ci) {
		if (l2backpack_shrinkListener != null) {
			l2backpack_shrinkListener.accept(count);
		}
	}

}
