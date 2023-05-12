package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.quickswap.merged.EnderSwitchContainer;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestContainer;
import dev.xkmc.l2backpack.events.BackpackSlotClickListener;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

public record PlayerSlot(ContainerType type, int slot, UUID uuid) {

	public static PlayerSlot ofInventory(int slot) {
		return new PlayerSlot(ContainerType.INVENTORY, slot, Util.NIL_UUID);
	}

	@Nullable
	public static PlayerSlot ofOtherInventory(int slot, int index, int wid, AbstractContainerMenu menu) {
		if (menu instanceof ChestMenu chest && chest.getContainer() instanceof PlayerEnderChestContainer) {
			return new PlayerSlot(ContainerType.ENDER, index, Util.NIL_UUID);
		}
		if (menu instanceof EnderSwitchContainer && index >= 36 && index < 63) {
			return new PlayerSlot(ContainerType.ENDER, index - 36, Util.NIL_UUID);
		}
		if (menu instanceof WorldChestContainer cont) {
			return new PlayerSlot(ContainerType.DIMENSION, cont.getColor() * 27 + index - 36, cont.getOwner());
		}
		return CuriosCompat.getPlayerSlot(slot, index, wid, menu)
				.orElse(null);
	}

	public static PlayerSlot read(FriendlyByteBuf buf) {
		ContainerType type = ContainerType.values()[buf.readInt()];
		int slot = buf.readInt();
		UUID id = buf.readUUID();
		return new PlayerSlot(type, slot, id);
	}

	@Nullable
	public static PlayerSlot ofCurio(Player player) {
		return CuriosCompat.getSearchBag(player, BackpackSlotClickListener::canOpen)
				.map(e -> new PlayerSlot(ContainerType.CURIO, e, Util.NIL_UUID)).orElse(null);
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeInt(type.ordinal());
		buf.writeInt(slot);
		buf.writeUUID(uuid);
	}

	public ItemStack getItem(Player player) {
		return type.getItem(player, this);
	}

}
