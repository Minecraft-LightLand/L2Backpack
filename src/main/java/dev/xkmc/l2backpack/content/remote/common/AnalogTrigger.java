package dev.xkmc.l2backpack.content.remote.common;

import dev.xkmc.l2backpack.init.advancement.BackpackTriggers;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class AnalogTrigger {

	public static void trigger(Level level, UUID id) {
		if (level.isClientSide())
			return;
		Proxy.getServer().map(e -> e.getPlayerList().getPlayer(id)).ifPresent(BackpackTriggers.ANALOG::trigger);
	}

}
