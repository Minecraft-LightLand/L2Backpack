package dev.xkmc.l2backpack.compat;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2screentracker.compat.CuriosTrackCompatImpl;
import dev.xkmc.l2screentracker.screen.source.PlayerSlot;
import dev.xkmc.l2screentracker.screen.source.SimpleSlotData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import java.util.Optional;
import java.util.function.Predicate;

public class CuriosCompat {

	public static Optional<Pair<ItemStack, PlayerSlot<?>>> getSlot(LivingEntity player, Predicate<ItemStack> pred) {
		if (ModList.get().isLoaded("curios")) {
			return getSlotImpl(player, pred);
		}
		return Optional.empty();
	}

	private static Optional<Pair<ItemStack, PlayerSlot<?>>> getSlotImpl(LivingEntity player, Predicate<ItemStack> pred) {
		var curio = CuriosApi.getCuriosInventory(player);
		if (curio.isPresent() && curio.resolve().isPresent()) {
			var e = curio.resolve().get().getEquippedCurios();
			for (int i = 0; i < e.getSlots(); i++) {
				ItemStack stack = e.getStackInSlot(i);
				if (pred.test(stack)) {
					return Optional.of(Pair.of(stack,
							new PlayerSlot<>(CuriosTrackCompatImpl.get().IS_CURIOS.get(),
									new SimpleSlotData(i))));
				}
			}
		}
		return Optional.empty();
	}

}
