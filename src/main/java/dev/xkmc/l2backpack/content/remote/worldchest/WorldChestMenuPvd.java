package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.content.remote.StorageContainer;
import dev.xkmc.l2backpack.content.remote.WorldStorage;
import dev.xkmc.l2library.util.ServerOnly;
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
		return stack.getDisplayName();
	}

	@ServerOnly
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		StorageContainer container = getContainer((ServerLevel) player.level).get();
		return new WorldChestContainer(id, inventory, container.container, container, getDisplayName(), null);
	}

	@ServerOnly
	private Optional<StorageContainer> getContainer(ServerLevel level) {
		CompoundTag tag = stack.getOrCreateTag();
		UUID id = tag.getUUID("owner_id");
		long pwd = tag.getLong("password");
		return WorldStorage.get(level).getOrCreateStorage(id, item.color.getId(), pwd);
	}

	@ServerOnly
	public void open() {
		item.refresh(stack, player);
		if (player.level.isClientSide() || getContainer((ServerLevel) player.level).isEmpty())
			return;
		NetworkHooks.openGui(player, this);
	}

}
