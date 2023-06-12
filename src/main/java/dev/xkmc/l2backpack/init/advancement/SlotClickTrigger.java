package dev.xkmc.l2backpack.init.advancement;

import dev.xkmc.l2library.serial.advancements.BaseCriterion;
import dev.xkmc.l2library.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2screentracker.screen.source.ItemSource;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class SlotClickTrigger extends BaseCriterion<SlotClickTrigger.Ins, SlotClickTrigger> {

	public static Ins fromGUI() {
		return new Ins(BackpackTriggers.SLOT_CLICK.getId(), ContextAwarePredicate.ANY);
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

	public SlotClickTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player, ItemSource<?> type, boolean keybind) {
		this.trigger(player, e -> (e.origin == null || e.origin == type) && e.keybind == keybind);
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, SlotClickTrigger> {

		@Nullable
		@SerialClass.SerialField
		private ItemSource<?> origin;

		@SerialClass.SerialField
		private boolean keybind = false;

		public Ins(ResourceLocation id, ContextAwarePredicate player) {
			super(id, player);
		}

	}

}
