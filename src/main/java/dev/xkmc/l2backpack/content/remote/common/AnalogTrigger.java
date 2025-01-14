package dev.xkmc.l2backpack.content.remote.common;

import dev.xkmc.l2backpack.init.registrate.LBTriggers;
import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.l2core.util.ServerProxy;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class AnalogTrigger {

	public static void trigger(Level level, UUID id) {
		if (level.isClientSide())
			return;
		ServerProxy.getServer().map(e -> e.getPlayerList().getPlayer(id)).ifPresent(LBTriggers.ANALOG.get()::trigger);
	}

}
