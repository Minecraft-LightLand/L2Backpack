package dev.xkmc.l2backpack.content.render;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class BackpackModel<T extends LivingEntity> extends HumanoidModel<T> {

	private final ModelPart part;

	public BackpackModel(ModelPart part) {
		super(part);
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
	public void setupAnim(T entity, float f0, float f1, float f3, float f4, float f5) {

	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0);
		PartDefinition root = mesh.getRoot().getChild("body");
		PartDefinition mainBody = root.addOrReplaceChild("main_body", CubeListBuilder.create().texOffs(0, 0).addBox(2.0F, 0.0F, 4.0F, 13.0F, 17.0F, 9.0F), PartPose.offset(-9.0F, 2F, -15F));
		mainBody.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 26).addBox(2.5F, -1F, 5.0F, 12.0F, 1.0F, 8.0F), PartPose.offset(0F, 0F, 0F));
		mainBody.addOrReplaceChild("button", CubeListBuilder.create().texOffs(0, 0).addBox(7.5F, 4.5F, 3.0F, 2.0F, 3.0F, 1.0F), PartPose.offset(0F, 0F, 0F));
		mainBody.addOrReplaceChild("pocketFace", CubeListBuilder.create().texOffs(0, 35).addBox(3.0F, 8.0F, 3.0F, 11, 9, 2), PartPose.offset(0.0F, 0F, 0F));
		mainBody.addOrReplaceChild("leftStrap", CubeListBuilder.create().texOffs(33, 28).addBox(1.0F, 5.0F, 5.0F, 3, 12, 7), PartPose.offset(0.0F, 0F, 0F));
		mainBody.addOrReplaceChild("rightStrap", CubeListBuilder.create().texOffs(44, 0).addBox(15.0F, 1.0F, 6.75F, 2, 16, 5), PartPose.offset(0.0F, 0F, 0F));
		return LayerDefinition.create(mesh, 64, 64);
	}
}
