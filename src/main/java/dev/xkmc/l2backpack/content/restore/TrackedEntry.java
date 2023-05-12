package dev.xkmc.l2backpack.content.restore;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.common.BaseOpenableContainer;
import dev.xkmc.l2backpack.content.common.ContainerType;
import dev.xkmc.l2backpack.content.remote.common.WorldStorage;
import dev.xkmc.l2backpack.content.remote.worldchest.SimpleStorageMenuPvd;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestContainer;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.registrate.BackpackMenu;
import dev.xkmc.l2backpack.network.restore.SetScreenToClient;
import dev.xkmc.l2library.base.tabs.curios.CuriosScreenCompat;
import dev.xkmc.l2library.util.annotation.ServerOnly;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public record TrackedEntry(ContainerType type, UUID id, int slot, String title) {

	public static TrackedEntry of(ContainerType type, UUID uuid, int color, @Nullable Component title) {
		String str = title == null ? "" : Component.Serializer.toJson(title);
		return new TrackedEntry(type, uuid, color, str);
	}

	@ServerOnly
	public boolean match(BaseOpenableContainer<?> current) {
		if (type == ContainerType.DIMENSION) {
			return current instanceof WorldChestContainer chest &&
					chest.getColor() == slot &&
					chest.getOwner().equals(id);
		} else return false;
	}

	public LayerPopType restoreServerMenu(ServerPlayer player) {
		Component comp = Component.Serializer.fromJson(title);
		if (type == ContainerType.INVENTORY) {
			L2Backpack.HANDLER.toClientPlayer(new SetScreenToClient(ScreenType.PLAYER), player);
			return LayerPopType.CLEAR;
		} else if (type == ContainerType.CURIO) {
			return CuriosCompat.openCurio(player) ? LayerPopType.CLEAR : LayerPopType.FAIL;
		} else if (type == ContainerType.CURIO_TAB) {
			CuriosScreenCompat.openScreen(player);
			return LayerPopType.CLEAR;
		} else if (type == ContainerType.ENDER) {
			if (comp == null) comp = Component.translatable("container.enderchest");
			NetworkHooks.openScreen(player, new SimpleMenuProvider((wid, inv, pl) ->
					ChestMenu.threeRows(wid, inv, pl.getEnderChestInventory()), comp));
			return LayerPopType.CLEAR;
		} else if (type == ContainerType.DIMENSION) {
			if (comp == null) {
				comp = Component.translatable(BackpackMenu.getLangKey(BackpackMenu.MT_WORLD_CHEST.get()));
			}
			var op = WorldStorage.get(player.getLevel()).getStorageWithoutPassword(id, slot);
			if (op.isPresent()) {
				NetworkHooks.openScreen(player, new SimpleStorageMenuPvd(comp, op.get()));
				return LayerPopType.REMAIN;
			}
		}
		return LayerPopType.FAIL;
	}

}
