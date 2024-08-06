package dev.xkmc.l2backpack.content.remote.dimensional;

import dev.xkmc.l2backpack.content.remote.common.LBSavedData;
import dev.xkmc.l2backpack.content.remote.common.StorageContainer;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2backpack.init.registrate.LBTriggers;
import dev.xkmc.l2core.util.ServerOnly;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.UUID;

public record DimensionalMenuPvd(ServerPlayer player, ItemStack stack, DimensionalItem item) implements MenuProvider {

	@Override
	public Component getDisplayName() {
		return stack.getHoverName();
	}

	@ServerOnly
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		StorageContainer container = getContainer((ServerLevel) player.level()).get();
		if (!container.id.equals(player.getUUID())) {
			LBTriggers.SHARE.get().trigger((ServerPlayer) player);
		}
		return new DimensionalContainer(id, inventory, container.get(), container, null);
	}

	@ServerOnly
	public Optional<StorageContainer> getContainer(ServerLevel level) {
		UUID id = LBItems.DC_OWNER_ID.get(stack);
		long pwd = LBItems.DC_PASSWORD.getOrDefault(stack, 0L);
		String lootStr = LBItems.DC_LOOT_ID.get(stack);
		long seed = LBItems.DC_LOOT_SEED.getOrDefault(stack, 0L);
		if (lootStr != null) {
			stack.remove(LBItems.DC_LOOT_ID);
			stack.remove(LBItems.DC_LOOT_SEED);
		}
		if (id == null) return Optional.empty();
		ResourceLocation loot = lootStr == null ? null : ResourceLocation.parse(lootStr);
		return LBSavedData.get(level).getOrCreateStorage(id, item.color.getId(), pwd, player, loot, seed);
	}

	@ServerOnly
	public void open() {
		item.refresh(stack, player);
		if (player.level().isClientSide() || getContainer((ServerLevel) player.level()).isEmpty())
			return;
		player.openMenu(this);
	}

}
