package dev.xkmc.l2backpack.content.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.arrowbag.ArrowBag;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemOnBackLayerRenderer<T extends LivingEntity, M extends HumanoidModel<T> & HeadedModel> extends RenderLayer<T, M> {
	private final float scaleX;
	private final float scaleY;
	private final float scaleZ;
	private final ItemInHandRenderer itemInHandRenderer;

	public ItemOnBackLayerRenderer(RenderLayerParent<T, M> parent, EntityModelSet set, ItemInHandRenderer renderer) {
		this(parent, set, 1.0F, 1.0F, 1.0F, renderer);
	}

	public ItemOnBackLayerRenderer(RenderLayerParent<T, M> parent, EntityModelSet set, float x, float y, float z, ItemInHandRenderer renderer) {
		super(parent);
		this.scaleX = x;
		this.scaleY = y;
		this.scaleZ = z;
		this.itemInHandRenderer = renderer;
	}

	public void render(PoseStack pose, MultiBufferSource buffer, int i, T entity, float f0, float f1, float f2, float f3, float f4, float f5) {
		ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
		ArrowBag item = null;
		if (stack.getItem() instanceof ArrowBag backpack) {
			item = backpack;
		} else if (entity instanceof Player player) {
			stack = CuriosCompat.getSlot(player, e -> e.getItem() instanceof ArrowBag);
			if (!stack.isEmpty())
				item = (ArrowBag) stack.getItem();
		}
		if (item == null) return;
		pose.pushPose();
		pose.scale(this.scaleX, this.scaleY, this.scaleZ);
		boolean flag = entity instanceof Villager || entity instanceof ZombieVillager;
		if (entity.isBaby() && !(entity instanceof Villager)) {
			pose.translate(0.0D, 0.03125D, 0.0D);
			pose.scale(0.7F, 0.7F, 0.7F);
			pose.translate(0.0D, 1.0D, 0.0D);
		}

		this.getParentModel().body.translateAndRotate(pose);
		translateToHead(pose, flag);
		this.itemInHandRenderer.renderItem(entity, stack, ItemTransforms.TransformType.HEAD, false, pose, buffer, i);
		pose.popPose();
	}

	public static void translateToHead(PoseStack pose, boolean villager) {
		float f = 1;
		pose.translate(0.0D, 1D, -0.25D);
		pose.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		pose.scale(f, -f, -f);
		if (villager) {
			pose.translate(0.0D, 0.1875D, 0.0D);
		}

	}
}