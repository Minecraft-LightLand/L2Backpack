package dev.xkmc.l2backpack.compat;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.LBConfig;
import dev.xkmc.l2menustacker.click.SlotClickHandler;
import dev.xkmc.l2menustacker.screen.base.ScreenTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContext;

public class SophisticatedClickListener extends SlotClickHandler {

	public SophisticatedClickListener() {
		super(L2Backpack.loc("sophisticated"));
	}

	@Override
	public void handle(ServerPlayer player, int index, int slot, int wid) {
		ItemStack carried = player.containerMenu.getCarried();
		if (!carried.isEmpty()) return;

		BackpackContext.Item ctx;
		ItemStack stack;
		if (slot >= 0) {
			ctx = new BackpackContext.Item("main", slot);
			stack = player.getInventory().getItem(slot);
		} else {
			var menu = player.containerMenu;
			if (wid == 0 || menu.containerId == 0 || wid != menu.containerId) return;
			if (!(menu instanceof ChestMenu chest)) return;
			if (!(chest.getContainer() instanceof PlayerEnderChestContainer ender)) return;
			stack = ender.getItem(index);
			ctx = new BackpackContext.Item("ender", index);
		}
		if (!stack.isEmpty()) {
			SimpleMenuProvider pvd = new SimpleMenuProvider((w, p, pl) -> new BackpackContainer(w, pl, ctx), stack.getHoverName());
			ScreenTracker.onServerOpen(player);
			player.openMenu(pvd, ctx::toBuffer);
		}

	}

	@Override
	public boolean isAllowed(ItemStack stack) {
		if (!LBConfig.SERVER.sophisticatedRightClickOpen.get()) return false;
		return stack.getItem() instanceof BackpackItem;
	}

}
