package dev.xkmc.l2backpack.init.advancement;

import dev.xkmc.l2backpack.init.registrate.LBTriggers;
import dev.xkmc.l2core.serial.advancements.BaseCriterion;
import dev.xkmc.l2core.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class BagInteractTrigger extends BaseCriterion<BagInteractTrigger.Ins, BagInteractTrigger> {

	public static Ins fromType(Type type) {
		Ins ans = new Ins();
		ans.type = type;
		return ans;
	}

	public static Ins fromType(Type type, Item... items) {
		Ins ans = fromType(type);
		ans.ingredient = Ingredient.of(items);
		return ans;
	}

	public BagInteractTrigger() {
		super(Ins.class);
	}

	public void trigger(ServerPlayer player, ItemStack stack, Type type, int count) {
		if (count > 0)
			this.trigger(player, e -> e.type == type &&
					(e.ingredient.isEmpty() || e.ingredient.test(stack)));
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, BagInteractTrigger> {

		@SerialField
		private Type type;

		@SerialField
		private Ingredient ingredient = Ingredient.EMPTY;

		public Ins() {
			super(LBTriggers.INTERACT.get());
		}
	}

	public enum Type {
		COLLECT, EXTRACT, LOAD, DUMP
	}
}
