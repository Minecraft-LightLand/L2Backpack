package dev.xkmc.l2backpack.init.data;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public enum Keys {
	OPEN("key.l2backpack.open", GLFW.GLFW_KEY_B),
	UP("key.l2backpack.up", GLFW.GLFW_KEY_UP),
	DOWN("key.l2backpack.down", GLFW.GLFW_KEY_DOWN),
	SWAP("key.l2backpack.swap", GLFW.GLFW_KEY_R);

	public final String id;
	public final int key;
	public final KeyMapping map;

	Keys(String id, int key) {
		this.id = id;
		this.key = key;
		map = new KeyMapping(id, key, "key.categories.l2backpack");
	}

}
