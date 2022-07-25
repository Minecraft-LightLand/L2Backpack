package dev.xkmc.l2backpack.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.common.inventory.CurioSlot;
import top.theillusivec4.curios.common.inventory.container.CuriosContainer;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class CuriosCompat {

	public static ItemStack getSlot(Player player, Predicate<ItemStack> pred) {
		if (ModList.get().isLoaded("curios")) {
			return getSlotImpl(player, pred);
		}
		return ItemStack.EMPTY;
	}

	public static Optional<Integer> getPlayerSlot(int slot, int index, int wid, AbstractContainerMenu menu) {
		if (ModList.get().isLoaded("curios")) {
			return getPlayerSlotImpl(slot, index, wid, menu);
		}
		return Optional.empty();
	}

	public static ItemStack getItemFromSlot(Player player, int slot) {
		if (ModList.get().isLoaded("curios")) {
			return getItemFromSlotImpl(player, slot);
		}
		return ItemStack.EMPTY;
	}

	public static Optional<Integer> getSearchBag(Player player, Predicate<ItemStack> pred) {
		if (ModList.get().isLoaded("curios")) {
			return getSearchBagImpl(player, pred);
		}
		return Optional.empty();
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

	private static Optional<Integer> getPlayerSlotImpl(int slot, int index, int wid, AbstractContainerMenu menu) {
		if (menu.containerId == wid && menu instanceof CuriosContainer cont) {
			Slot s = cont.getSlot(index);
			if (s instanceof CurioSlot curioSlot) {
				int slotIndex = getSlotIndexInContainer(cont.player, curioSlot.getIdentifier());
				if (slotIndex < 0) {
					return Optional.empty();
				}
				int val = slotIndex + curioSlot.getSlotIndex();
				return Optional.of(val);
			}
		}
		return Optional.empty();
	}

	private static ItemStack getItemFromSlotImpl(Player player, int slot) {
		var curios = CuriosApi.getCuriosHelper().getEquippedCurios(player);
		if (curios.isPresent() && curios.resolve().isPresent()) {
			var e = curios.resolve().get();
			return e.getStackInSlot(slot);
		}
		return ItemStack.EMPTY;
	}

	private static int getSlotIndexInContainer(Player player, String id) {
		var curiosLazy = CuriosApi.getCuriosHelper().getCuriosHandler(player);
		if (!curiosLazy.isPresent() || !curiosLazy.resolve().isPresent()) {
			return -1;
		}
		Map<String, ICurioStacksHandler> curios = curiosLazy.resolve().get().getCurios();
		int index = 0;

		for (ICurioStacksHandler stacksHandler : curios.values()) {
			if (stacksHandler.getIdentifier().equals(id)) {
				return index;
			}
			index += stacksHandler.getSlots();
		}
		return -1;
	}

	private static Optional<Integer> getSearchBagImpl(Player player, Predicate<ItemStack> pred) {
		var curio = CuriosApi.getCuriosHelper().getEquippedCurios(player);
		if (curio.isPresent() && curio.resolve().isPresent()) {
			var e = curio.resolve().get();
			for (int i = 0; i < e.getSlots(); i++) {
				ItemStack stack = e.getStackInSlot(i);
				if (pred.test(stack)) {
					return Optional.of(i);
				}
			}
		}
		return Optional.empty();
	}

}
