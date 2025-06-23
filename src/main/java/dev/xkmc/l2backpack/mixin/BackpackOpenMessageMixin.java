package dev.xkmc.l2backpack.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.xkmc.l2backpack.compat.SophisticatedCompat;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackOpenMessage")
public class BackpackOpenMessageMixin {

	@WrapOperation(method = "handleMessage", at = @At(value = "INVOKE", target = "Lnet/p3pp3rf1y/sophisticatedbackpacks/network/BackpackOpenMessage;findAndOpenFirstBackpack(Lnet/minecraft/server/level/ServerPlayer;)V"))
	private static void l2backpack$wrapFindFirstBackpack(ServerPlayer player, Operation<Void> original) {
		if (player.containerMenu != player.inventoryMenu) return;
		SophisticatedCompat.ENDER_LOCK.set(Unit.INSTANCE);
		original.call(player);
		SophisticatedCompat.ENDER_LOCK.remove();
	}

}
