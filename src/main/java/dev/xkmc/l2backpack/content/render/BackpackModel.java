package dev.xkmc.l2backpack.content.render;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.LivingEntity;

public class BackpackModel extends AgeableListModel<LivingEntity> {

	private final ModelPart part;

	public BackpackModel(ModelPart part) {
		this.part = part;
	}

	@Override
	protected Iterable<ModelPart> headParts() {
		return ImmutableList.of();
	}

	@Override
	protected Iterable<ModelPart> bodyParts() {
		return ImmutableList.of(part);
	}

	@Override
	public void setupAnim(LivingEntity entity, float f0, float f1, float f3, float f4, float f5) {

	}

	public static LayerDefinition createBodyLayer() {
		return null; //TODO
	}
}
