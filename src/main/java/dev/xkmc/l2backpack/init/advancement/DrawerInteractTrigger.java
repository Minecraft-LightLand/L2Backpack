package dev.xkmc.l2backpack.init.advancement;

import dev.xkmc.l2backpack.network.drawer.DrawerInteractToServer;
import dev.xkmc.l2library.serial.advancements.BaseCriterion;
import dev.xkmc.l2library.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class DrawerInteractTrigger extends BaseCriterion<DrawerInteractTrigger.Ins, DrawerInteractTrigger> {

	public static Ins fromType(DrawerInteractToServer.Type type) {
		Ins ans = new Ins(BackpackTriggers.DRAWER.getId(), ContextAwarePredicate.ANY);
		ans.type = type;
		return ans;
	}

	public DrawerInteractTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player, DrawerInteractToServer.Type type) {
		this.trigger(player, e -> (e.type == null || e.type == type));
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, DrawerInteractTrigger> {

		@Nullable
		@SerialClass.SerialField
		private DrawerInteractToServer.Type type;

		public Ins(ResourceLocation id, ContextAwarePredicate player) {
			super(id, player);
		}

	}

}
