package dev.xkmc.l2backpack.compat;

import com.mojang.datafixers.util.Pair;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapManager;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2backpack.content.remote.dimensional.DimensionalInvWrapper;
import dev.xkmc.l2backpack.content.remote.dimensional.DimensionalItem;
import dev.xkmc.l2backpack.events.ArrowBagEvents;
import dev.xkmc.l2backpack.init.registrate.LBBlocks;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2library.util.GenericItemStack;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.events.event.GolemEquipEvent;
import dev.xkmc.modulargolems.events.event.GolemHandleItemEvent;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GolemCompat {

	public static void register() {
		NeoForge.EVENT_BUS.register(GolemCompat.class);
	}

	private static boolean canEquip(ItemStack stack) {
		if (stack.getItem() instanceof BaseBagItem) {
			return true;
		}
		if (stack.getItem() instanceof DimensionalItem) {
			return LBItems.DC_OWNER_ID.get(stack) != null;
		}
		return false;
	}

	@Nullable
	private static GenericItemStack<DimensionalItem> getBackpack(AbstractGolemEntity<?, ?> golem) {
		for (var e : List.of(EquipmentSlot.CHEST, EquipmentSlot.OFFHAND)) {
			ItemStack stack = golem.getItemBySlot(e);
			if (stack.getItem() instanceof DimensionalItem item) {
				return new GenericItemStack<>(item, stack);
			}
		}
		var opt = CuriosCompat.getSlot(golem,
				e -> e.getItem() instanceof DimensionalItem);
		if (opt.isPresent()) {
			ItemStack stack = opt.get().getFirst();
			if (stack.getItem() instanceof DimensionalItem item) {
				return new GenericItemStack<>(item, stack);
			}
		}
		return null;
	}

	@SubscribeEvent
	public static void onEquip(GolemEquipEvent event) {
		if (canEquip(event.getStack())) {
			ItemStack back = event.getEntity().getItemBySlot(EquipmentSlot.CHEST);
			if (back.isEmpty() || canEquip(back))
				event.setSlot(EquipmentSlot.CHEST, 1);
			else event.setSlot(EquipmentSlot.OFFHAND, 1);
		}
	}

	@SubscribeEvent
	public static void onHandleItem(GolemHandleItemEvent event) {
		if (event.getItem().isRemoved()) return;
		ItemStack stack = event.getItem().getItem();
		if (stack.isEmpty()) return;
		var backpack = getBackpack(event.getEntity());
		if (backpack == null) return;
		ServerLevel level = (ServerLevel) event.getEntity().level();
		var cont = backpack.item().getContainer(backpack.stack(), level);
		if (cont.isEmpty()) return;
		var storage = cont.get();
		var handler = new DimensionalInvWrapper(storage.get(), storage.id);
		ItemStack remain = ItemHandlerHelper.insertItem(handler, stack, false);
		event.getItem().setItem(remain);
	}


	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onArrowFind(ArrowBagEvents.ArrowFindEvent event) {
		if (!(event.getEntity() instanceof AbstractGolemEntity<?, ?> golem)) return;
		IQuickSwapToken<?> token = QuickSwapManager.getToken(event.getEntity(), event.getStack(), false);
		if (token != null && token.type() == QuickSwapTypes.ARROW) {
			var arrows = token.getList();
			for (int i = 0; i < 9; i++) {
				ItemStack stack = arrows.get(i).getStack();
				if (event.setProjectile(Pair.of(stack, token::shrink))) {
					return;
				}
			}
		}
		var backpack = getBackpack(golem);
		if (backpack == null) return;
		if (event.getEntity().level() instanceof ServerLevel level) {
			var cont = backpack.item().getContainer(backpack.stack(), level);
			if (cont.isEmpty()) return;
			var storage = cont.get();
			for (int i = 0; i < storage.get().getContainerSize(); i++) {
				ItemStack stack = storage.get().getItem(i);
				if (event.setProjectile(Pair.of(stack, x -> storage.get().setChanged()))) {
					return;
				}
			}
		}
	}

	public static void genBlockTag(RegistrateTagsProvider.IntrinsicImpl<Block> pvd) {
		pvd.addTag(MGTagGen.POTENTIAL_DST).add(LBBlocks.DIMENSIONAL.get());
	}

}
