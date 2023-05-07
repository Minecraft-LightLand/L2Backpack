package dev.xkmc.l2backpack.init.advancement;

import dev.xkmc.l2library.serial.advancements.BaseCriterion;
import dev.xkmc.l2library.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class SharedDriveTrigger extends BaseCriterion<SharedDriveTrigger.Ins, SharedDriveTrigger> {

	public static Ins ins() {
		return new Ins(BackpackTriggers.SHARE.getId(), EntityPredicate.Composite.ANY);
	}

	public SharedDriveTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player) {
		this.trigger(player, e -> true);
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, SharedDriveTrigger> {

		public Ins(ResourceLocation id, EntityPredicate.Composite player) {
			super(id, player);
		}

	}

}
