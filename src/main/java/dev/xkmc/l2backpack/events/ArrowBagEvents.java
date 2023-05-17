package dev.xkmc.l2backpack.events;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapManager;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapType;
import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.function.IntConsumer;

@Mod.EventBusSubscriber(modid = L2Backpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArrowBagEvents {

	private static final ThreadLocal<Pair<ItemStack, IntConsumer>> TEMP = new ThreadLocal<>();

	@SubscribeEvent
	public static void onProjectileSearch(LivingGetProjectileEvent event) {
		if (!(event.getProjectileWeaponItemStack().getItem() instanceof ProjectileWeaponItem weapon)) return;
		IQuickSwapToken token = QuickSwapManager.getToken(event.getEntity(), false);
		if (token == null) return;
		if (token.type() != QuickSwapType.ARROW) return;
		List<ItemStack> arrows = token.getList();
		var pred = weapon.getAllSupportedProjectiles();
		int selected = token.getSelected();
		ItemStack stack = arrows.get(selected);
		if (pred.test(stack)) {
			TEMP.set(Pair.of(stack, token::shrink));
			event.setProjectileItemStack(stack);
		}
	}

	public static void shrink(ItemStack stack, int count) {
		if (TEMP.get() != null && TEMP.get().getFirst() == stack) {
			TEMP.get().getSecond().accept(count);
		}
	}
}
