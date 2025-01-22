package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.LBConfig;
import dev.xkmc.l2backpack.init.data.LBTagGen;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = L2Backpack.MODID, bus = EventBusSubscriber.Bus.GAME)
public class StartUpGiveItemEvents {

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		if (event.getEntity().tickCount != 10) return;
		if (!(event.getEntity() instanceof ServerPlayer sp)) return;
		var time = sp.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
		if (time > 100) return;
		var adv = sp.server.getAdvancements().get(L2Backpack.loc("detection"));
		if (adv == null) return;
		var prog = sp.getAdvancements().getOrStartProgress(adv);
		if (prog.isDone()) return;
		int target = LBConfig.SERVER.startupBackpackCondition.get();
		var list = sp.getInventory().items;
		int count = 0;
		for (ItemStack stack : list) {
			if (!stack.isEmpty()) {
				count++;
			}
		}
		if (count < target) return;
		int initialRow = Math.max(LBConfig.SERVER.initialRows.get(), (count - 1) / 9 + 1);
		ItemStack stack = LBItems.DC_ROW.set(LBItems.BACKPACKS[DyeColor.WHITE.ordinal()].asStack(), initialRow);
		var ans = NonNullList.withSize(initialRow * 9, ItemStack.EMPTY);
		int index = 0;
		for (int i = 0; i < list.size(); i++) {
			ItemStack item = list.get(i);
			if (!item.isEmpty() && item.getItem().canFitInsideContainerItems() &&
					!item.is(LBTagGen.BACKPACK_BLACKLIST)) {
				ans.set(index++, item.copy());
				list.set(i, ItemStack.EMPTY);
			}
		}
		BaseBagItem.setItems(stack, ans);
		sp.getInventory().placeItemBackInInventory(stack);
	}

}
