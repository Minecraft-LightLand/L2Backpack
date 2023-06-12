package dev.xkmc.l2backpack.content.restore;

import dev.xkmc.l2backpack.content.remote.common.WorldStorage;
import dev.xkmc.l2backpack.content.remote.worldchest.SimpleStorageMenuPvd;
import dev.xkmc.l2backpack.init.registrate.BackpackMenu;
import dev.xkmc.l2screentracker.screen.base.LayerPopType;
import dev.xkmc.l2screentracker.screen.track.TrackedEntryType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class DimensionTrace extends TrackedEntryType<DimensionTraceData> {

	@Override
	public LayerPopType restoreMenuNotifyClient(ServerPlayer player, DimensionTraceData data, @Nullable Component comp) {
		if (comp == null) {
			comp = Component.translatable(BackpackMenu.getLangKey(BackpackMenu.MT_WORLD_CHEST.get()));
		}
		var op = WorldStorage.get((ServerLevel) player.level()).getStorageWithoutPassword(data.uuid(), data.color());
		if (op.isPresent()) {
			NetworkHooks.openScreen(player, new SimpleStorageMenuPvd(comp, op.get()));
			return LayerPopType.REMAIN;
		}
		return LayerPopType.FAIL;
	}

}
