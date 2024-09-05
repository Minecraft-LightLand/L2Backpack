package dev.xkmc.l2backpack.content.remote.common;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SerialClass
public class StorageContainer implements ContainerListener {

	public UUID id;
	public int color;

	@SerialField
	private final List<ItemStack> tag = new ArrayList<>();

	@SerialField
	long password = -1L;

	@SerialField
	boolean init = false;

	private SimpleContainer container;

	public SimpleContainer get() {
		if (container == null) {
			this.container = new SimpleContainer(54);
			for (int i = 0; i < tag.size(); i++) {
				this.container.setItem(i, tag.get(i));
			}
		}
		container.addListener(this);
		return container;
	}

	@Override
	public void containerChanged(Container cont) {
		tag.clear();
		for (int i = 0; i < container.getContainerSize(); i++)
			tag.add(container.getItem(i));
	}

}
