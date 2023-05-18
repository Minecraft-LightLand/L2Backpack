package dev.xkmc.l2backpack.content.restore;

import dev.xkmc.l2library.init.events.screen.source.ItemSourceData;

import java.util.UUID;

public record DimensionSourceData(int color, int slot, UUID uuid)
		implements ItemSourceData<DimensionSourceData> {

}
