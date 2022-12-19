package dev.xkmc.l2backpack.init.advancement;

import dev.xkmc.l2backpack.content.common.ContainerType;
import dev.xkmc.l2library.base.advancements.BaseCriterion;
import dev.xkmc.l2library.base.advancements.BaseCriterionInstance;
import dev.xkmc.l2library.serial.SerialClass;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class SlotClickTrigger extends BaseCriterion<SlotClickTrigger.Ins, SlotClickTrigger> {

	public static Ins fromGUI() {
		return new Ins(BackpackTriggers.SLOT_CLICK.getId(), EntityPredicate.Composite.ANY);
	}

	public static Ins fromKeyBind() {
		Ins ans = fromGUI();
		ans.keybind = true;
		return ans;
	}

	public static Ins fromBackpack(ContainerType type) {
		Ins ans = fromGUI();
		ans.origin = type;
		return ans;
	}

	public SlotClickTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player, ContainerType type, boolean keybind) {
		this.trigger(player, e -> (e.origin == null || e.origin == type) && e.keybind == keybind);
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, SlotClickTrigger> {

		@Nullable
		@SerialClass.SerialField
		private ContainerType origin;

		@SerialClass.SerialField
		private boolean keybind = false;

		public Ins(ResourceLocation id, EntityPredicate.Composite player) {
			super(id, player);
		}

	}

}
