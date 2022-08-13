package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.remote.WorldStorage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public enum ContainerType {
	INVENTORY((player, slot) -> player.getInventory().getItem(slot.slot())),
	ENDER((player, slot) -> player.getEnderChestInventory().getItem(slot.slot())),
	DIMENSION((player, slot) -> player.getLevel().isClientSide() ? ItemStack.EMPTY :
			WorldStorage.get((ServerLevel) player.getLevel())
					.getStorageWithoutPassword(slot.uuid(), slot.slot() / 27)
					.map(e -> e.container.getItem(slot.slot() % 27)).orElse(ItemStack.EMPTY)),
	CURIO(CuriosCompat::getItemFromSlot);

	private final BiFunction<Player, PlayerSlot, ItemStack> getter;

	ContainerType(BiFunction<Player, PlayerSlot, ItemStack> getter) {
		this.getter = getter;
	}

	public ItemStack getItem(Player player, PlayerSlot slot) {
		return getter.apply(player, slot);
	}
}
