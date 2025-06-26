package dev.xkmc.l2backpack.compat;

import dev.xkmc.l2backpack.content.remote.player.EnderTickEvent;
import dev.xkmc.l2backpack.init.data.LBConfig;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2backpack.init.registrate.LBMisc;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ITickableUpgrade;

public class SophisticatedCompat {

	public static final ThreadLocal<Unit> ENDER_LOCK = new ThreadLocal<>();

	public static void init() {
		PlayerInventoryProvider.get().addPlayerInventoryHandler("ender",
				l -> PlayerInventoryHandler.SINGLE_IDENTIFIER,
				(player, id) -> getEnderSize(player),
				(player, id, slot) -> getEnderInv(player, slot),
				false, false, false, false);
	}

	private static int getEnderSize(Player player) {
		if (player instanceof ServerPlayer) {
			return player.getEnderChestInventory().getContainerSize();
		}
		return LBMisc.ENDER_SYNC.type().getOrCreate(player).getItems(player).size();
	}

	private static ItemStack getEnderInv(Player player, int index) {
		if (ENDER_LOCK.get() != null) {
			return ItemStack.EMPTY;
		}
		if (player instanceof ServerPlayer) {
			return player.getEnderChestInventory().getItem(index);
		}
		return LBMisc.ENDER_SYNC.type().getOrCreate(player).getItems(player).get(index);
	}

	@SubscribeEvent
	public static void onEnderTick(EnderTickEvent event) {
		var player = event.getPlayer();
		if (player.isSpectator() || player.isDeadOrDying()) return;
		if (!LBConfig.SERVER.sophisticatedEnderTicking.get()) return;
		if (!hasEnder(player)) return;
		BackpackWrapper.fromStack(event.getStack()).getUpgradeHandler().getWrappersThatImplement(ITickableUpgrade.class)
				.forEach((upgrade) -> upgrade.tick(player, player.level(), player.blockPosition()));
	}

	private static boolean hasEnder(ServerPlayer player) {
		ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
		if (stack.is(LBItems.ENDER_BACKPACK.get())) return true;
		var pairOpt = CuriosCompat.getSlot(player, e -> e.is((LBItems.ENDER_BACKPACK.get())));
		return pairOpt.isPresent();
	}

}
