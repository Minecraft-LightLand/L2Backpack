package dev.xkmc.l2backpack.events;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapManager;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapType;
import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.IntConsumer;

@Mod.EventBusSubscriber(modid = L2Backpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArrowBagEvents {

	public static final ThreadLocal<Pair<ItemStack, IntConsumer>> TEMP = new ThreadLocal<>();

	@SubscribeEvent
	public static void onProjectileSearch(LivingGetProjectileEvent event) {
		if (!(event.getProjectileWeaponItemStack().getItem() instanceof ProjectileWeaponItem weapon)) return;
		ArrowFindEvent finder = new ArrowFindEvent(event.getProjectileWeaponItemStack(), weapon, event.getEntity());
		MinecraftForge.EVENT_BUS.post(finder);
		var arrow = finder.arrow;
		if (arrow != null) {
			TEMP.set(arrow);
			event.setProjectileItemStack(arrow.getFirst());
		}
	}

	@SubscribeEvent
	public static void onArrowFind(ArrowFindEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		IQuickSwapToken token = QuickSwapManager.getToken(event.getEntity(), event.getStack(), false);
		if (token == null) return;
		if (token.type() != QuickSwapType.ARROW) return;
		List<ItemStack> arrows = token.getList();
		int selected = token.getSelected();
		ItemStack stack = arrows.get(selected);
		event.setProjectile(Pair.of(stack, token::shrink));
	}

	public static void shrink(ItemStack stack, int count) {
		if (TEMP.get() != null && TEMP.get().getFirst() == stack) {
			TEMP.get().getSecond().accept(count);
		}
	}

	public static class ArrowFindEvent extends Event {

		private final ItemStack stack;
		private final ProjectileWeaponItem weapon;
		private final LivingEntity entity;

		private Pair<ItemStack, IntConsumer> arrow;

		public ArrowFindEvent(ItemStack stack, ProjectileWeaponItem weapon, LivingEntity entity) {
			this.stack = stack;
			this.weapon = weapon;
			this.entity = entity;
		}

		public LivingEntity getEntity() {
			return entity;
		}

		public ItemStack getStack() {
			return stack;
		}

		public boolean setProjectile(Pair<ItemStack, IntConsumer> arrow) {
			if (weapon.getAllSupportedProjectiles().test(arrow.getFirst())) {
				this.arrow = arrow;
				return true;
			}
			return false;
		}

		@Nullable
		public Pair<ItemStack, IntConsumer> getArrow() {
			return arrow;
		}

	}

}
