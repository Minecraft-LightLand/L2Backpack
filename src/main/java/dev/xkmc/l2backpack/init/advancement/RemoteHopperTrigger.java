package dev.xkmc.l2backpack.init.advancement;

import dev.xkmc.l2library.serial.advancements.BaseCriterion;
import dev.xkmc.l2library.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class RemoteHopperTrigger extends BaseCriterion<RemoteHopperTrigger.Ins, RemoteHopperTrigger> {

	public static Ins ins() {
		return new Ins(BackpackTriggers.REMOTE.getId(), ContextAwarePredicate.ANY);
	}

	public RemoteHopperTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player) {
		this.trigger(player, e -> true);
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, RemoteHopperTrigger> {

		public Ins(ResourceLocation id, ContextAwarePredicate player) {
			super(id, player);
		}

	}

}
