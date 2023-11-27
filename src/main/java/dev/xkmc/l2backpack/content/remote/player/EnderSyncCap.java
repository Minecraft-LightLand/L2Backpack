package dev.xkmc.l2backpack.content.remote.player;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapItem;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapType;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2library.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2library.capability.player.PlayerCapabilityNetworkHandler;
import dev.xkmc.l2library.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SerialClass
public class EnderSyncCap extends PlayerCapabilityTemplate<EnderSyncCap> {


	public static final Capability<EnderSyncCap> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});

	public static final PlayerCapabilityHolder<EnderSyncCap> HOLDER =
			new PlayerCapabilityHolder<>(new ResourceLocation(L2Backpack.MODID, "player_ender"), CAPABILITY,
					EnderSyncCap.class, EnderSyncCap::new, PlayerCapabilityNetworkHandler::new);

	// in both client and server
	public NonNullList<ItemStack> clientEnderInv = NonNullList.withSize(27, ItemStack.EMPTY);

	public EnderSyncCap() {
	}

	@Override
	public void tick() {
		if (world.isClientSide()) return;
		List<Pair<Integer, ItemStack>> changes = new ArrayList<>();
		for (int i = 0; i < 27; i++) {
			ItemStack stack = player.getEnderChestInventory().getItem(i);
			if (!ItemStack.isSameItemSameTags(stack, clientEnderInv.get(i))) {
				clientEnderInv.set(i, stack.copy());
				changes.add(Pair.of(i, stack));
			}
		}
		if (changes.size() > 0) {
			L2Backpack.HANDLER.toClientPlayer(new EnderSyncPacket(changes), (ServerPlayer) player);
		}
	}

	public static void register() {

	}

	public List<ItemStack> getItems() {
		if (world.isClientSide())
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
	public IQuickSwapToken getToken(QuickSwapType type) {
		for (ItemStack stack : getItems()) {
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
