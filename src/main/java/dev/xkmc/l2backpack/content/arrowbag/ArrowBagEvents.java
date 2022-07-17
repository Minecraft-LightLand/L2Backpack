package dev.xkmc.l2backpack.content.arrowbag;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.events.SetArrowToServer;
import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.IntConsumer;

public class ArrowBagEvents {

	private static final ThreadLocal<Pair<ItemStack, IntConsumer>> TEMP = new ThreadLocal<>();

	@SubscribeEvent
	public static void onProjectileSearch(LivingGetProjectileEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!(event.getProjectileWeaponItemStack().getItem() instanceof ProjectileWeaponItem weapon)) return;
		ItemStack bag = ArrowBagManager.getArrowBag(player);
		if (bag.isEmpty()) return;
		List<ItemStack> arrows = ArrowBag.getItems(bag);
		var pred = weapon.getAllSupportedProjectiles();
		int selected = ArrowBag.getSelected(bag);
		ItemStack stack = arrows.get(selected);
		if (pred.test(stack)) {
			TEMP.set(Pair.of(stack, c -> shrinkStack(bag, selected, c)));
			event.setProjectileItemStack(stack);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void keyEvent(InputEvent.Key event) {
		if (ArrowBagOverlay.isScreenOn() && Minecraft.getInstance().options.keyShift.isDown()) {
			if (event.getKey() == GLFW.GLFW_KEY_UP && event.getAction() == InputConstants.PRESS) {
				L2Backpack.HANDLER.toServer(new SetArrowToServer(-1));
			} else if (event.getKey() == GLFW.GLFW_KEY_DOWN && event.getAction() == InputConstants.PRESS) {
				L2Backpack.HANDLER.toServer(new SetArrowToServer(-2));
			} else for (int i = 0; i < 9; i++) {
				if (Minecraft.getInstance().options.keyHotbarSlots[i].consumeClick()) {
					L2Backpack.HANDLER.toServer(new SetArrowToServer(i));
				}
			}
		}
	}

	private static void shrinkStack(ItemStack offhand, int index, int count) {
		List<ItemStack> list = ArrowBag.getItems(offhand);
		list.get(index).shrink(count);
		ArrowBag.setItems(offhand, list);
	}

	public static void shrink(ItemStack stack, int count) {
		if (TEMP.get() != null && TEMP.get().getFirst() == stack) {
			TEMP.get().getSecond().accept(count);
		}
	}
}
