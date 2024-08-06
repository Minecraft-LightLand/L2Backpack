package dev.xkmc.l2backpack.content.remote.player;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapItem;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2core.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SerialClass
public class EnderSyncCap extends PlayerCapabilityTemplate<EnderSyncCap> {

	// in both client and server
	public NonNullList<ItemStack> clientEnderInv = NonNullList.withSize(27, ItemStack.EMPTY);

	public EnderSyncCap() {
	}

	@Override
	public void tick(Player player) {
		if (player.level().isClientSide()) return;
		List<Pair<Integer, ItemStack>> changes = new ArrayList<>();
		for (int i = 0; i < 27; i++) {
			ItemStack stack = player.getEnderChestInventory().getItem(i);
			if (!ItemStack.isSameItemSameComponents(stack, clientEnderInv.get(i))) {
				clientEnderInv.set(i, stack.copy());
				changes.add(Pair.of(i, stack));
			}
		}
		if (!changes.isEmpty()) {
			L2Backpack.HANDLER.toClientPlayer(EnderSyncPacket.of(changes), (ServerPlayer) player);
		}
	}

	public static void register() {

	}

	public List<ItemStack> getItems(Player player) {
		if (player.level().isClientSide())
			return clientEnderInv;
		List<ItemStack> ans = new ArrayList<>();
		for (int i = 0; i < 27; i++) {
			ans.add(player.getEnderChestInventory().getItem(i));
		}
		return ans;
	}

	// client only
	public void setItem(int slot, ItemStack stack) {
		clientEnderInv.set(slot, stack);
	}

	@Nullable
	public IQuickSwapToken<?> getToken(Player player, QuickSwapType type) {
		for (ItemStack stack : getItems(player)) {
			if (stack.getItem() instanceof IQuickSwapItem item && item instanceof BaseBagItem) {
				var token = item.getTokenOfType(stack, player, type);
				if (token != null) {
					return token;
				}
			}
		}
		return null;
	}

}
