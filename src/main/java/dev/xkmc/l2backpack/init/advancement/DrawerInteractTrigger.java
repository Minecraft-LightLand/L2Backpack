package dev.xkmc.l2backpack.init.advancement;

import dev.xkmc.l2backpack.init.registrate.LBTriggers;
import dev.xkmc.l2backpack.network.ClickInteractToServer;
import dev.xkmc.l2core.serial.advancements.BaseCriterion;
import dev.xkmc.l2core.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class DrawerInteractTrigger extends BaseCriterion<DrawerInteractTrigger.Ins, DrawerInteractTrigger> {

	public static Ins fromType(ClickInteractToServer.Type type) {
		Ins ans = new Ins();
		ans.type = type;
		return ans;
	}

	public DrawerInteractTrigger() {
		super(Ins.class);
	}

	public void trigger(ServerPlayer player, ClickInteractToServer.Type type) {
		this.trigger(player, e -> (e.type == null || e.type == type));
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, DrawerInteractTrigger> {

		@Nullable
		@SerialField
		private ClickInteractToServer.Type type;

		public Ins() {
			super(LBTriggers.DRAWER.get());
		}
	}

}
