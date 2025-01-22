package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.content.backpack.BackpackItem;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.BackpackConfig;
import dev.xkmc.l2backpack.init.data.TagGen;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = L2Backpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StartUpGiveItemEvents {

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || event.player.tickCount != 10) return;
		if (!(event.player instanceof ServerPlayer sp)) return;
		var time = sp.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
		if (time > 100) return;
		var adv = sp.server.getAdvancements().getAdvancement(new ResourceLocation(L2Backpack.MODID, "detection"));
		if (adv == null) return;
		var prog = sp.getAdvancements().getOrStartProgress(adv);
		if (prog.isDone()) return;
		int target = BackpackConfig.COMMON.startupBackpackCondition.get();
		var list = sp.getInventory().items;
		int count = 0;
		for (ItemStack stack : list) {
			if (!stack.isEmpty()) {
				count++;
			}
		}
		if (count < target) return;
		int initialRow = Math.max(BackpackConfig.COMMON.initialRows.get(), (count - 1) / 9 + 1);
		ItemStack stack = BackpackItem.setRow(BackpackItems.BACKPACKS[DyeColor.WHITE.ordinal()].asStack(), initialRow);
		var ans = NonNullList.withSize(initialRow * 9, ItemStack.EMPTY);
		int index = 0;
		for (int i = 0; i < list.size(); i++) {
			if (!list.get(i).isEmpty() &&
					list.get(i).getItem().canFitInsideContainerItems() &&
					!stack.is(TagGen.BACKPACK_BLACKLIST)) {
				ans.set(index++, list.get(i).copy());
				list.set(i, ItemStack.EMPTY);
			}
		}
		BackpackItem.setItems(stack, ans);
		sp.getInventory().placeItemBackInInventory(stack);
	}

}
