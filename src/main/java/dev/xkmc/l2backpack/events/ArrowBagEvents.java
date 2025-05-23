package dev.xkmc.l2backpack.events;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapManager;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingGetProjectileEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;

@EventBusSubscriber(modid = L2Backpack.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ArrowBagEvents {

	@SubscribeEvent
	public static void onProjectileSearch(LivingGetProjectileEvent event) {
		if (!(event.getProjectileWeaponItemStack().getItem() instanceof ProjectileWeaponItem weapon)) return;
		ArrowFindEvent finder = new ArrowFindEvent(event.getProjectileWeaponItemStack(), weapon, event.getEntity());
		NeoForge.EVENT_BUS.post(finder);
		var arrow = finder.arrow;
		if (arrow != null) {
			((ItemStackShrinkProvider) (Object) arrow.getFirst()).l2backpack$setShrinkListener(arrow.getSecond());
			event.setProjectileItemStack(arrow.getFirst());
		}
	}

	@SubscribeEvent
	public static void onArrowFind(ArrowFindEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		IQuickSwapToken<?> token = QuickSwapManager.getToken(event.getEntity(), event.getStack(), false);
		if (token == null) return;
		if (token.type() != QuickSwapTypes.ARROW) return;
		var arrows = token.getList();
		int selected = token.getSelected();
		var entry = arrows.get(selected);
		ItemStack stack = entry.getStack();
		if (stack.isEmpty()) return;
		event.setProjectile(Pair.of(stack, token::shrink));
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
