package dev.xkmc.l2backpack.content.restore;

import dev.xkmc.l2backpack.content.remote.common.WorldStorage;
import dev.xkmc.l2screentracker.screen.source.ItemSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DimensionItemSource extends ItemSource<DimensionSourceData> {

	@Override
	public ItemStack getItem(Player player, DimensionSourceData data) {
		if (player.level().isClientSide()) return ItemStack.EMPTY;
		return WorldStorage.get((ServerLevel) player.level())
				.getStorageWithoutPassword(data.uuid(), data.color())
				.map(e -> e.container.getItem(data.slot())).orElse(ItemStack.EMPTY);
	}

}
