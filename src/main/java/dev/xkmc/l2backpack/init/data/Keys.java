package dev.xkmc.l2backpack.init.data;

import dev.xkmc.l2screentracker.init.L2ScreenTracker;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = L2ScreenTracker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum Keys {
	OPEN("key.l2backpack.open", GLFW.GLFW_KEY_B);

	public final String id;
	public final int key;
	public final KeyMapping map;

	Keys(String id, int key) {
		this.id = id;
		this.key = key;
		map = new KeyMapping(id, key, "key.categories.l2backpack");
	}

	@SubscribeEvent
	public static void registerKeys(RegisterKeyMappingsEvent event) {
		for (Keys k : Keys.values())
			event.register(k.map);
	}

}
