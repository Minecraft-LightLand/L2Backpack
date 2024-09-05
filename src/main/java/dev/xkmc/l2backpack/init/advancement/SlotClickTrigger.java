package dev.xkmc.l2backpack.init.advancement;

import dev.xkmc.l2backpack.init.registrate.LBTriggers;
import dev.xkmc.l2core.serial.advancements.BaseCriterion;
import dev.xkmc.l2core.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2menustacker.screen.source.ItemSource;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class SlotClickTrigger extends BaseCriterion<SlotClickTrigger.Ins, SlotClickTrigger> {

	public static Ins fromGUI() {
		return new Ins();
	}

	public static Ins fromKeyBind() {
		Ins ans = fromGUI();
		ans.keybind = true;
		return ans;
	}

	public static Ins fromBackpack(ItemSource<?> type) {
		Ins ans = fromGUI();
		ans.origin = type;
		return ans;
	}

	public SlotClickTrigger() {
		super(Ins.class);
	}

	public void trigger(ServerPlayer player, ItemSource<?> type, boolean keybind) {
		this.trigger(player, e -> (e.origin == null || e.origin == type) && e.keybind == keybind);
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, SlotClickTrigger> {

		@Nullable
		@SerialField
		private ItemSource<?> origin;

		@SerialField
		private boolean keybind = false;

		public Ins() {
			super(LBTriggers.SLOT_CLICK.get());
		}
	}

}
