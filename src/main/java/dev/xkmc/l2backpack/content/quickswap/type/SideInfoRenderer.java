package dev.xkmc.l2backpack.content.quickswap.type;

import dev.xkmc.l2library.base.overlay.SelectionSideBar;
import net.minecraft.world.entity.player.Player;

public interface SideInfoRenderer {

	void renderSide(SelectionSideBar.Context ctx, int x, int y, Player player, OverlayToken<?> hover);

}
