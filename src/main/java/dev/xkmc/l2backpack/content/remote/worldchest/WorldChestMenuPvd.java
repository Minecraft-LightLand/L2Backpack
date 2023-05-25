package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.content.remote.common.StorageContainer;
import dev.xkmc.l2backpack.content.remote.common.WorldStorage;
import dev.xkmc.l2backpack.init.advancement.BackpackTriggers;
import dev.xkmc.l2library.util.annotation.ServerOnly;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;
import java.util.UUID;

public record WorldChestMenuPvd(ServerPlayer player, ItemStack stack, WorldChestItem item) implements MenuProvider {

	@Override
	public Component getDisplayName() {
		return stack.getHoverName();
	}

	@ServerOnly
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		StorageContainer container = item.getContainer(stack, (ServerLevel) player.level).get();
		if (!container.id.equals(player.getUUID())) {
			BackpackTriggers.SHARE.trigger((ServerPlayer) player);
		}
		return new WorldChestContainer(id, inventory, container.container, container, null, getDisplayName());
	}

	@ServerOnly
	public void open() {
		item.refresh(stack, player);
		if (player.level.isClientSide() || item.getContainer(stack, (ServerLevel) player.level).isEmpty())
			return;
		NetworkHooks.openScreen(player, this);
	}

}
