package dev.xkmc.l2backpack.content.restore;

import dev.xkmc.l2backpack.content.remote.common.WorldStorage;
import dev.xkmc.l2backpack.content.remote.worldchest.SimpleStorageMenuPvd;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestContainer;
import dev.xkmc.l2backpack.init.registrate.BackpackMenu;
import dev.xkmc.l2library.init.events.screen.base.LayerPopType;
import dev.xkmc.l2library.init.events.screen.track.TrackedEntryType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class DimensionTrace extends TrackedEntryType<DimensionTraceData> {

	@Override
	public LayerPopType restoreMenuNotifyClient(ServerPlayer player, DimensionTraceData data, @Nullable Component comp) {
		if (comp == null) {
			comp = Component.translatable(BackpackMenu.getLangKey(BackpackMenu.MT_WORLD_CHEST.get()));
		}
		var op = WorldStorage.get(player.getLevel()).getStorageWithoutPassword(data.uuid(), data.color());
		if (op.isPresent()) {
			NetworkHooks.openScreen(player, new SimpleStorageMenuPvd(comp, op.get()));
			return LayerPopType.REMAIN;
		}
		return LayerPopType.FAIL;
	}

	@Override
	public boolean match(AbstractContainerMenu current, DimensionTraceData data) {
		return current instanceof WorldChestContainer chest &&
				chest.getColor() == data.color() &&
				chest.getOwner().equals(data.uuid());
	}

}
