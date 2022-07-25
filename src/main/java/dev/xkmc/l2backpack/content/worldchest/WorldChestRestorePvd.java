package dev.xkmc.l2backpack.content.worldchest;

import dev.xkmc.l2library.util.annotation.ServerOnly;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;

public record WorldChestRestorePvd(ServerPlayer player, WorldChestContainer cont) implements MenuProvider {

	@Override
	public Component getDisplayName() {
		return cont.title;
	}

	@ServerOnly
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return new WorldChestContainer(id, inventory, cont.container, cont.storage, getDisplayName());
	}

	@ServerOnly
	public void open() {
		if (player.level.isClientSide())
			return;
		NetworkHooks.openScreen(player, this);
	}

}
