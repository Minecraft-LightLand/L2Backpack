package dev.xkmc.l2backpack.init.advancement;

import dev.xkmc.l2library.serial.advancements.BaseCriterion;
import dev.xkmc.l2library.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class AnalogSignalTrigger extends BaseCriterion<AnalogSignalTrigger.Ins, AnalogSignalTrigger> {

	public static Ins ins() {
		return new Ins(BackpackTriggers.ANALOG.getId(), ContextAwarePredicate.ANY);
	}

	public AnalogSignalTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player) {
		this.trigger(player, e -> true);
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, AnalogSignalTrigger> {

		public Ins(ResourceLocation id, ContextAwarePredicate player) {
			super(id, player);
		}

	}

}
