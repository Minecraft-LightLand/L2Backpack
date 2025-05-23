package dev.xkmc.l2backpack.events;

import java.util.function.IntConsumer;

public interface ItemStackShrinkProvider {

	void l2backpack$setShrinkListener(IntConsumer cons);

}
