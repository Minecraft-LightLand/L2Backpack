package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2library.base.menu.BaseContainerMenu;
import dev.xkmc.l2library.base.menu.SpriteManager;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Function;

public class BaseOpenableContainer<T extends BaseOpenableContainer<T>> extends BaseContainerMenu<T> {

	protected final Player player;

	protected BaseOpenableContainer(MenuType<?> type, int wid, Inventory plInv, SpriteManager manager, Function<T, SimpleContainer> factory) {
		super(type, wid, plInv, manager, factory, false);
		player = plInv.player;
	}

}
