package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2library.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2library.base.menu.base.SpriteManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;
import java.util.function.Function;

public class BaseOpenableContainer<T extends BaseOpenableContainer<T>> extends BaseContainerMenu<T> {

	protected final Player player;

	@Nullable
	public final Component title;

	protected BaseOpenableContainer(MenuType<?> type, int wid, Inventory plInv, SpriteManager manager, Function<T, SimpleContainer> factory, @Nullable Component title) {
		super(type, wid, plInv, manager, factory, false);
		player = plInv.player;
		this.title = title;
	}

}
