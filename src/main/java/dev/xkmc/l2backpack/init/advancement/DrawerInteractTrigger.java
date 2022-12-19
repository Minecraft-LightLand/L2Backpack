package dev.xkmc.l2backpack.init.advancement;

import dev.xkmc.l2backpack.init.advancement.util.BaseCriterion;
import dev.xkmc.l2backpack.init.advancement.util.BaseCriterionInstance;
import dev.xkmc.l2backpack.network.drawer.DrawerInteractToServer;
import dev.xkmc.l2library.serial.SerialClass;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class DrawerInteractTrigger extends BaseCriterion<DrawerInteractTrigger.Ins, DrawerInteractTrigger> {

	public static Ins fromType(DrawerInteractToServer.Type type) {
		Ins ans = new Ins(BackpackTriggers.DRAWER.getId(), EntityPredicate.Composite.ANY);
		ans.type = type;
		return ans;
	}

	public static Ins fromOthers() {
		Ins ans = new Ins(BackpackTriggers.DRAWER.getId(), EntityPredicate.Composite.ANY);
		ans.other = true;
		return ans;
	}

	public DrawerInteractTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player, DrawerInteractToServer.Type type, boolean other) {
		this.trigger(player, e -> (e.type == null || e.type == type) && (!e.other || other));
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, DrawerInteractTrigger> {

		@Nullable
		@SerialClass.SerialField
		private DrawerInteractToServer.Type type;

		private boolean other = false;

		public Ins(ResourceLocation id, EntityPredicate.Composite player) {
			super(id, player);
		}

	}

}
