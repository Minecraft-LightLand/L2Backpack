package dev.xkmc.l2backpack.compat;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2library.init.compat.CuriosTrackCompatImpl;
import dev.xkmc.l2library.init.events.screen.source.PlayerSlot;
import dev.xkmc.l2library.init.events.screen.source.SimpleSlotData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import java.util.Optional;
import java.util.function.Predicate;

public class CuriosCompat {

	public static void init() {
		if (ModList.get().isLoaded("curios"))
			initImpl();
	}

	public static Optional<Pair<ItemStack, PlayerSlot<?>>> getSlot(Player player, Predicate<ItemStack> pred) {
		if (ModList.get().isLoaded("curios")) {
			return getSlotImpl(player, pred);
		}
		return Optional.empty();
	}

	private static void initImpl() {
		InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BACK.getMessageBuilder().build());
	}

	private static Optional<Pair<ItemStack, PlayerSlot<?>>> getSlotImpl(Player player, Predicate<ItemStack> pred) {
		var curio = CuriosApi.getCuriosHelper().getEquippedCurios(player);
		if (curio.isPresent() && curio.resolve().isPresent()) {
			var e = curio.resolve().get();
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
