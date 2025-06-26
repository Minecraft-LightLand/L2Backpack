package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.content.quickswap.handswap.HandswapItem;
import dev.xkmc.l2backpack.init.L2Backpack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = L2Backpack.MODID, bus = EventBusSubscriber.Bus.GAME)
public class HandswapEvents {

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		var pl = event.getEntity();
		if (pl.level().isClientSide()) return;
		var stack = HandswapItem.getToken(pl);
		if (stack.isEmpty()) return;
		HandswapItem.check(stack, pl);
	}

}
