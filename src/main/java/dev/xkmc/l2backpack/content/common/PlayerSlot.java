package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestContainer;
import dev.xkmc.l2backpack.events.MiscEventHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public record PlayerSlot(ContainerType type, int slot) {

	public static PlayerSlot ofInventory(int slot) {
		return new PlayerSlot(ContainerType.INVENTORY, slot);
	}

	@Nullable
	public static PlayerSlot ofOtherInventory(int slot, int index, int wid, AbstractContainerMenu menu) {
		if (menu instanceof ChestMenu chest && chest.getContainer() instanceof PlayerEnderChestContainer) {
			return new PlayerSlot(ContainerType.ENDER, index);
		}
		if (menu instanceof WorldChestContainer cont && cont.isOpenedByOwner()) {
			return new PlayerSlot(ContainerType.DIMENSION, cont.getColor() * 27 + index - 36);
		}
		return CuriosCompat.getPlayerSlot(slot, index, wid, menu)
				.map(e -> new PlayerSlot(ContainerType.CURIO, e))
				.orElse(null);
	}

	public static PlayerSlot read(FriendlyByteBuf buf) {
		ContainerType type = ContainerType.values()[buf.readInt()];
		int slot = buf.readInt();
		return new PlayerSlot(type, slot);
	}

	@Nullable
	public static PlayerSlot ofCurio(Player player) {
		return CuriosCompat.getSearchBag(player, MiscEventHandler::canOpen)
				.map(e -> new PlayerSlot(ContainerType.CURIO, e)).orElse(null);
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeInt(type.ordinal());
		buf.writeInt(slot);
	}

	public ItemStack getItem(Player player) {
		return type.getItem(player, slot);
	}
}
