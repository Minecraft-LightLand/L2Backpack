package dev.xkmc.l2backpack.init.advancement;

import dev.xkmc.l2library.base.advancements.BaseCriterion;
import dev.xkmc.l2library.base.advancements.BaseCriterionInstance;
import dev.xkmc.l2library.serial.SerialClass;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;

public class BagInteractTrigger extends BaseCriterion<BagInteractTrigger.Ins, BagInteractTrigger> {

	public static Ins fromType(Type type) {
		Ins ans = new Ins(BackpackTriggers.INTERACT.getId(), EntityPredicate.Composite.ANY);
		ans.type = type;
		return ans;
	}

	public static Ins fromType(Type type, Item... items) {
		Ins ans = fromType(type);
		ans.ingredient = Ingredient.of(items);
		return ans;
	}

	public BagInteractTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player, ItemStack stack, Type type, int count) {
		if (count > 0)
			this.trigger(player, e -> e.type == type &&
					(e.ingredient.isEmpty() || e.ingredient.test(stack)));
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, BagInteractTrigger> {

		@SerialClass.SerialField
		private Type type;

		@SerialClass.SerialField
		private Ingredient ingredient = Ingredient.EMPTY;

		public Ins(ResourceLocation id, EntityPredicate.Composite player) {
			super(id, player);
		}

	}

	public enum Type {
		COLLECT, EXTRACT, LOAD, DUMP
	}
}
