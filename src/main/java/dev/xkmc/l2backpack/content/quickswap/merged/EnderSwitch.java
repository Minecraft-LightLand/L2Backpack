package dev.xkmc.l2backpack.content.quickswap.merged;

import dev.xkmc.l2backpack.content.common.PlayerSlot;
import dev.xkmc.l2backpack.content.quickswap.common.SimpleMenuPvd;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class EnderSwitch extends MultiSwitch {

	public EnderSwitch(Properties props) {
		super(props);
	}

	@Override
	public void open(ServerPlayer player, PlayerSlot slot, ItemStack stack) {
		new SimpleMenuPvd(player, slot, stack, EnderSwitchContainer::new).open();
	}

}
