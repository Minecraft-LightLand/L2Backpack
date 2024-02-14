package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.content.remote.common.StorageContainer;
import dev.xkmc.l2backpack.content.remote.common.WorldStorage;
import dev.xkmc.l2backpack.init.advancement.BackpackTriggers;
import dev.xkmc.l2library.util.annotation.ServerOnly;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
		StorageContainer container = getContainer((ServerLevel) player.level()).get();
		if (!container.id.equals(player.getUUID())) {
			BackpackTriggers.SHARE.trigger((ServerPlayer) player);
		}
		return new WorldChestContainer(id, inventory, container.container, container, null);
	}

	@ServerOnly
	public Optional<StorageContainer> getContainer(ServerLevel level) {
		CompoundTag tag = stack.getOrCreateTag();
		UUID id = tag.getUUID("owner_id");
		long pwd = tag.getLong("password");
		long seed = 0;
		ResourceLocation loot = null;
		if (tag.contains("loot")) {
			loot = new ResourceLocation(tag.getString("loot"));
			tag.remove("loot");
			seed = tag.getLong("seed");
			tag.remove("seed");
		}
		return WorldStorage.get(level).getOrCreateStorage(level, id, item.color.getId(), pwd, player, loot, seed);
	}

	@ServerOnly
	public void open() {
		item.refresh(stack, player);
		if (player.level().isClientSide() || getContainer((ServerLevel) player.level()).isEmpty())
			return;
		NetworkHooks.openScreen(player, this);
	}

}
