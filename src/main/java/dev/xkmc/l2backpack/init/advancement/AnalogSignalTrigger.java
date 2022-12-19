package dev.xkmc.l2backpack.init.advancement;

import dev.xkmc.l2backpack.init.advancement.util.BaseCriterion;
import dev.xkmc.l2backpack.init.advancement.util.BaseCriterionInstance;
import dev.xkmc.l2library.serial.SerialClass;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class AnalogSignalTrigger extends BaseCriterion<AnalogSignalTrigger.Ins, AnalogSignalTrigger> {

	public static Ins ins() {
		return new Ins(BackpackTriggers.ANALOG.getId(), EntityPredicate.Composite.ANY);
	}

	public AnalogSignalTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player) {
		this.trigger(player, e -> true);
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, AnalogSignalTrigger> {

		public Ins(ResourceLocation id, EntityPredicate.Composite player) {
			super(id, player);
		}

	}

}
