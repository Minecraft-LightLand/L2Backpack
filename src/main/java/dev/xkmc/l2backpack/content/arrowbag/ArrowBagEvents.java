package dev.xkmc.l2backpack.content.arrowbag;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.mixin.ItemStackMixin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.function.IntConsumer;

public class ArrowBagEvents {

	private static final ThreadLocal<Pair<ItemStack, IntConsumer>> TEMP = new ThreadLocal<>();

	@SubscribeEvent
	public static void onProjectileSearch(LivingGetProjectileEvent event) {
		ItemStack offhand = event.getEntity().getOffhandItem();
		if (!(offhand.getItem() instanceof ArrowBag)) return;
		if (!(event.getProjectileWeaponItemStack().getItem() instanceof ProjectileWeaponItem weapon)) return;
		List<ItemStack> arrows = ArrowBag.getItems(offhand);
		var pred = weapon.getAllSupportedProjectiles();
		for (int i = 0; i < arrows.size(); i++) {
			ItemStack stack = arrows.get(i);
			if (pred.test(stack)) {
				int finalI = i;
				TEMP.set(Pair.of(stack, c -> shrinkStack(offhand, finalI, c)));
				event.setProjectileItemStack(stack);
			}
		}
	}

	private static void shrinkStack(ItemStack offhand, int index, int count) {
		List<ItemStack> list = ArrowBag.getItems(offhand);
		list.get(index).shrink(count);
		ArrowBag.setItems(offhand, list);
	}

	public static void shrink(ItemStack stack,int count) {
		if (TEMP.get()!=null && TEMP.get().getFirst() == stack){
			TEMP.get().getSecond().accept(count);
		}
	}
}
