package dev.xkmc.l2backpack.init.advancement;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class CriterionBuilder {

	@Deprecated
	public static CriterionBuilder none() {
		return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.ANY));
	}

	public static CriterionBuilder item(Item item) {
		return one(InventoryChangeTrigger.TriggerInstance.hasItems(item));
	}

	public static CriterionBuilder item(TagKey<Item> item) {
		return one(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(item).build()));
	}

	public static CriterionBuilder one(CriterionTriggerInstance instance) {
		return new CriterionBuilder(RequirementsStrategy.AND).add(instance);
	}

	public static CriterionBuilder and() {
		return new CriterionBuilder(RequirementsStrategy.AND);
	}

	public static CriterionBuilder or() {
		return new CriterionBuilder(RequirementsStrategy.OR);
	}

	private final RequirementsStrategy req;
	private final List<CriterionTriggerInstance> list = new ArrayList<>();

	private CriterionBuilder(RequirementsStrategy req) {
		this.req = req;
	}

	private CriterionBuilder add(CriterionTriggerInstance instance) {
		list.add(instance);
		return this;
	}

	void accept(String id, Advancement.Builder builder) {
		int index = 0;
		if (list.size() > 1) {
			builder.requirements(req);
		}
		for (var c : list) {
			builder.addCriterion((index++) + "", c);
		}
	}

}
