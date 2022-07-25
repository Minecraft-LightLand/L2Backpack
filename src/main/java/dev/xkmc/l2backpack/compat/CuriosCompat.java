package dev.xkmc.l2backpack.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Predicate;

public class CuriosCompat {

	public static ItemStack getSlot(Player player, Predicate<ItemStack> pred) {
		if (ModList.get().isLoaded("curios")) {
			return getSlotImpl(player, pred);
		}
		return ItemStack.EMPTY;
	}

	private static ItemStack getSlotImpl(Player player, Predicate<ItemStack> pred) {
		var curio = CuriosApi.getCuriosHelper().getEquippedCurios(player);
		if (curio.isPresent() && curio.resolve().isPresent()) {
			var e = curio.resolve().get();
			for (int i = 0; i < e.getSlots(); i++) {
				ItemStack stack = e.getStackInSlot(i);
				if (pred.test(stack)) {
					return stack;
				}
			}
		}
		return ItemStack.EMPTY;
	}

}
