package dev.xkmc.l2backpack.compat;

import dev.xkmc.l2backpack.content.remote.player.EnderSyncCap;
import dev.xkmc.l2backpack.content.remote.player.EnderTickEvent;
import dev.xkmc.l2backpack.init.data.BackpackConfig;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ITickableUpgrade;

import java.util.Set;
import java.util.function.Function;

public class SophisticatedCompat {

	public static final ThreadLocal<Unit> ENDER_LOCK = new ThreadLocal<>();

	public static void init() {
		PlayerInventoryProvider.get().addPlayerInventoryHandler("ender",
				// to make it cross-version compatible
				(Function) SophisticatedCompat::idSupplier,
				(player, id) -> getEnderSize(player),
				(player, id, slot) -> getEnderInv(player, slot),
				false, false, false, false);
	}

	private static Set<String> idSupplier(Object obj) {
		return PlayerInventoryHandler.SINGLE_IDENTIFIER;
	}

	private static int getEnderSize(Player player) {
		if (player instanceof ServerPlayer) {
			return player.getEnderChestInventory().getContainerSize();
		}
		return EnderSyncCap.HOLDER.get(player).getItems().size();
	}

	private static ItemStack getEnderInv(Player player, int index) {
		if (ENDER_LOCK.get() != null) {
			return ItemStack.EMPTY;
		}
		if (player instanceof ServerPlayer) {
			return player.getEnderChestInventory().getItem(index);
		}
		return EnderSyncCap.HOLDER.get(player).getItems().get(index);
	}

	@SubscribeEvent
	public static void onEnderTick(EnderTickEvent event) {
		var player = event.getPlayer();
		if (player.isSpectator() || player.isDeadOrDying()) return;
		if (!BackpackConfig.COMMON.sophisticatedEnderTicking.get()) return;
		if (!hasEnder(player)) return;
		event.getStack().getCapability(CapabilityBackpackWrapper.getCapabilityInstance()).ifPresent((wrapper) ->
				wrapper.getUpgradeHandler().getWrappersThatImplement(ITickableUpgrade.class).forEach((upgrade) ->
						upgrade.tick(player, player.level(), player.blockPosition())));
	}

	private static boolean hasEnder(ServerPlayer player) {
		ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
		if (stack.is(BackpackItems.ENDER_BACKPACK.get())) return true;
		var pairOpt = CuriosCompat.getSlot(player, e -> e.is((BackpackItems.ENDER_BACKPACK.get())));
		return pairOpt.isPresent();
	}

}
