package dev.xkmc.l2backpack.compat;

import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestInvWrapper;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestItem;
import dev.xkmc.l2backpack.init.registrate.BackpackBlocks;
import dev.xkmc.l2library.util.code.GenericItemStack;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.events.event.GolemEquipEvent;
import dev.xkmc.modulargolems.events.event.GolemHandleItemEvent;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.List;

public class GolemCompat {

	public static void register() {
		MinecraftForge.EVENT_BUS.register(GolemCompat.class);
	}

	private static boolean canEquip(ItemStack stack) {
		if (stack.getItem() instanceof BaseBagItem) {
			return true;
		}
		if (stack.getItem() instanceof WorldChestItem) {
			return stack.hasTag() && stack.getOrCreateTag().contains("owner_id");
		}
		return false;
	}

	@Nullable
	private static GenericItemStack<WorldChestItem> getBackpack(AbstractGolemEntity<?, ?> golem) {
		for (var e : List.of(EquipmentSlot.CHEST, EquipmentSlot.OFFHAND)) {
			ItemStack stack = golem.getItemBySlot(e);
			if (stack.getItem() instanceof WorldChestItem item) {
				return new GenericItemStack<>(item, stack);
			}
		}
		var opt = CuriosCompat.getSlot(golem,
				e -> e.getItem() instanceof WorldChestItem);
		if (opt.isPresent()) {
			ItemStack stack = opt.get().getFirst();
			if (stack.getItem() instanceof WorldChestItem item) {
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
		var handler = new WorldChestInvWrapper(storage.container, storage.id);
		ItemStack remain = ItemHandlerHelper.insertItem(handler, stack, false);
		event.getItem().setItem(remain);
	}

	public static void genBlockTag(RegistrateTagsProvider.IntrinsicImpl<Block> pvd) {
		pvd.addTag(MGTagGen.POTENTIAL_DST).add(BackpackBlocks.WORLD_CHEST.get());
	}

}
