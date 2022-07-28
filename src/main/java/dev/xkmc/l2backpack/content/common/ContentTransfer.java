package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.List;

public class ContentTransfer {

	public static int transfer(List<ItemStack> list, IItemHandler cap) {
		int n = list.size();
		int count = 0;
		for (int i = 0; i < n; i++) {
			ItemStack stack = list.get(i);
			count += stack.getCount();
			stack = ItemHandlerHelper.insertItemStacked(cap, stack, false);
			count -= stack.getCount();
			list.set(i, stack);
		}
		return count;
	}

	public static int transfer(Item item, int count, IItemHandler cap) {
		int maxSize = item.getMaxStackSize();
		while (count > 0) {
			int step = Math.min(maxSize, count);
			ItemStack toInsert = new ItemStack(item, step);
			ItemStack remainer = ItemHandlerHelper.insertItemStacked(cap, toInsert, false);
			count = count - step + remainer.getCount();
			if (!remainer.isEmpty()) {
				return count;
			}
		}
		return 0;
	}

	public static InteractionResult blockInteract(UseOnContext context, Quad item) {
		Player player = context.getPlayer();
		if (player != null) {
			BlockPos pos = context.getClickedPos();
			BlockEntity target = context.getLevel().getBlockEntity(pos);
			if (target != null) {
				var capLazy = target.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
				if (capLazy.resolve().isPresent()) {
					var cap = capLazy.resolve().get();
					item.click(player, context.getItemInHand(), context.getLevel().isClientSide(), player.isShiftKeyDown(), true, cap);
					return InteractionResult.SUCCESS;
				}
			}
		}
		return InteractionResult.PASS;
	}

	public static void onDump(Player player, int count) {
		if (player instanceof ServerPlayer serverPlayer) {
			serverPlayer.sendSystemMessage(LangData.IDS.DUMP_FEEDBACK.get(count), ChatType.GAME_INFO);
		}
	}

	public static void onExtract(Player player, int count) {
		if (player instanceof ServerPlayer serverPlayer) {
			serverPlayer.sendSystemMessage(LangData.IDS.EXTRACT_FEEDBACK.get(count), ChatType.GAME_INFO);
		}
	}

	public static void onCollect(Player player, int count) {
		if (player instanceof ServerPlayer serverPlayer) {
			serverPlayer.sendSystemMessage(LangData.IDS.COLLECT_FEEDBACK.get(count), ChatType.GAME_INFO);
		}
	}

	public interface Quad {

		void click(Player player, ItemStack stack, boolean client, boolean shift, boolean right, @Nullable IItemHandler target);

	}

}
