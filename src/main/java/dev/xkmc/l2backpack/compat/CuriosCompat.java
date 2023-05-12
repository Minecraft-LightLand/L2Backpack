package dev.xkmc.l2backpack.compat;

import dev.xkmc.l2backpack.content.common.ContainerType;
import dev.xkmc.l2backpack.content.common.PlayerSlot;
import dev.xkmc.l2library.base.tabs.curios.CuriosListMenu;
import net.minecraft.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkHooks;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.common.inventory.CurioSlot;
import top.theillusivec4.curios.common.inventory.container.CuriosContainer;
import top.theillusivec4.curios.common.inventory.container.CuriosContainerProvider;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class CuriosCompat {

	public static void init() {
		if (ModList.get().isLoaded("curios"))
			initImpl();
	}

	public static ItemStack getSlot(Player player, Predicate<ItemStack> pred) {
		if (ModList.get().isLoaded("curios")) {
			return getSlotImpl(player, pred);
		}
		return ItemStack.EMPTY;
	}

	public static Optional<PlayerSlot> getPlayerSlot(int slot, int index, int wid, AbstractContainerMenu menu) {
		if (ModList.get().isLoaded("curios")) {
			return getPlayerSlotImpl(slot, index, wid, menu);
		}
		return Optional.empty();
	}

	public static ItemStack getItemFromSlot(Player player, PlayerSlot slot) {
		if (ModList.get().isLoaded("curios")) {
			return getItemFromSlotImpl(player, slot.slot());
		}
		return ItemStack.EMPTY;
	}

	public static Optional<Integer> getSearchBag(Player player, Predicate<ItemStack> pred) {
		if (ModList.get().isLoaded("curios")) {
			return getSearchBagImpl(player, pred);
		}
		return Optional.empty();
	}

	public static boolean openCurio(ServerPlayer player) {
		if (ModList.get().isLoaded("curios")) {
			openCurioImpl(player);
			return true;
		}
		return false;
	}

	private static void initImpl() {
		InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BACK.getMessageBuilder().build());
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

	private static Optional<PlayerSlot> getPlayerSlotImpl(int slot, int index, int wid, AbstractContainerMenu menu) {
		if (menu.containerId == wid && menu instanceof CuriosContainer cont) {
			Slot s = cont.getSlot(index);
			if (s instanceof CurioSlot curioSlot) {
				int slotIndex = getSlotIndexInContainer(cont.player, curioSlot.getIdentifier());
				if (slotIndex < 0) {
					return Optional.empty();
				}
				int val = slotIndex + curioSlot.getSlotIndex();
				return Optional.of(new PlayerSlot(ContainerType.CURIO, val, Util.NIL_UUID));
			}
		}
		if (menu.containerId == wid && menu instanceof CuriosListMenu cont) {
			Slot s = cont.getSlot(index);
			if (s instanceof CurioSlot curioSlot) {
				int slotIndex = getSlotIndexInContainer(cont.inventory.player, curioSlot.getIdentifier());
				if (slotIndex < 0) {
					return Optional.empty();
				}
				int val = slotIndex + curioSlot.getSlotIndex();
				return Optional.of(new PlayerSlot(ContainerType.CURIO_TAB, val, Util.NIL_UUID));
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

	private static void openCurioImpl(ServerPlayer player) {
		NetworkHooks.openScreen(player, new CuriosContainerProvider());
	}

}
