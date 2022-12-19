package dev.xkmc.l2backpack.init.advancement;

import dev.xkmc.l2library.base.advancements.BaseCriterion;
import dev.xkmc.l2library.base.advancements.BaseCriterionInstance;
import dev.xkmc.l2library.serial.SerialClass;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ExitMenuTrigger extends BaseCriterion<ExitMenuTrigger.Ins, ExitMenuTrigger> {

	public static Ins exitOne() {
		return new Ins(BackpackTriggers.EXIT_MENU.getId(), EntityPredicate.Composite.ANY);
	}

	public static Ins exitAll() {
		Ins ans = exitOne();
		ans.all = true;
		return ans;
	}

	public ExitMenuTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player, boolean all) {
		this.trigger(player, e -> e.all == all);
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, ExitMenuTrigger> {

		@SerialClass.SerialField
		private boolean all = false;

		public Ins(ResourceLocation id, EntityPredicate.Composite player) {
			super(id, player);
		}

	}

}
