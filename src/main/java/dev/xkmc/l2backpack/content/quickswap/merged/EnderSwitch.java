package dev.xkmc.l2backpack.content.quickswap.merged;

import dev.xkmc.l2backpack.content.common.PlayerSlot;
import dev.xkmc.l2backpack.content.quickswap.common.SimpleMenuPvd;
import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnderSwitch extends MultiSwitch {

	public EnderSwitch(Properties props) {
		super(props);
	}

	@Override
	public void open(ServerPlayer player, PlayerSlot slot, ItemStack stack) {
		new SimpleMenuPvd(player, slot, stack, (a, b, c, d, e) -> new EnderSwitchContainer(a, b, player.getEnderChestInventory(), c, d, e)).open();
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		LangData.addInfo(list, LangData.Info.ENDER_SWITCH_INFO);
	}

}
