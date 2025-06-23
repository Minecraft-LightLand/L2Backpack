package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.content.quickswap.handswap.HandswapItem;
import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = L2Backpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HandswapEvents {

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;
		var pl = event.player;
		if (pl.level().isClientSide()) return;
		var stack = HandswapItem.getToken(pl);
		if (stack.isEmpty()) return;
		HandswapItem.check(stack, pl);
	}

}
