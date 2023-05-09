package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.Keys;
import dev.xkmc.l2backpack.network.drawer.DrawerInteractToServer;
import dev.xkmc.l2library.init.data.L2Keys;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

public class ClientEventHandler {

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void keyEvent(InputEvent.Key event) {
		if (Minecraft.getInstance().screen == null && Proxy.getClientPlayer() != null && Keys.OPEN.map.isDown()) {
			if (BackpackSlotClickListener.canOpen(Proxy.getClientPlayer().getItemBySlot(EquipmentSlot.CHEST)) ||
					!CuriosCompat.getSlot(Proxy.getClientPlayer(), BackpackSlotClickListener::canOpen).isEmpty())
				L2Backpack.SLOT_CLICK.keyBind();
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onScreenLeftClick(ScreenEvent.MouseButtonReleased.Pre event) {
		if (onRelease(event)) {
			event.setCanceled(true);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onScreenRightClick(ScreenEvent.MouseButtonPressed.Pre event) {
		if (onPress(event)) {
			event.setCanceled(true);
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static boolean onRelease(ScreenEvent.MouseButtonReleased.Pre event) {
		Screen screen = event.getScreen();
		if (screen instanceof AbstractContainerScreen cont) {
			Slot slot = cont.getSlotUnderMouse();
			if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				if (insertItem(event, cont, slot, true)) return true;
			}
			if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && slot != null) {
				return slot.getItem().getItem() instanceof BaseDrawerItem &&
						!cont.getMenu().getCarried().isEmpty();
			}
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	private static boolean onPress(ScreenEvent.MouseButtonPressed.Pre event) {
		Screen screen = event.getScreen();
		if (screen instanceof AbstractContainerScreen cont) {
			Slot slot = cont.getSlotUnderMouse();
			if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				if (insertItem(event, cont, slot, false)) return true;
			}
			if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				if (extractItem(event, cont, slot)) return true;
				if (slot != null) {
					return slot.getItem().getItem() instanceof BaseDrawerItem &&
							!cont.getMenu().getCarried().isEmpty();
				}
			}
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	private static boolean insertItem(ScreenEvent event, AbstractContainerScreen<?> cont, @Nullable Slot slot, boolean perform) {
		if (slot == null || !slot.allowModification(Proxy.getClientPlayer())) {
			return false;
		}
		ItemStack drawerStack = slot.getItem();
		ItemStack stack = cont.getMenu().getCarried();
		if (drawerStack.getItem() instanceof BaseDrawerItem drawer) {
			if (stack.isEmpty()) return false;
			if (stack.hasTag()) return true;
			if (drawer.canSetNewItem(drawerStack)) {
				if (perform)
					sendDrawerPacket(DrawerInteractToServer.Type.SET, cont, slot);
				return true;
			}
			if (BaseDrawerItem.canAccept(drawerStack, stack)) {
				if (perform)
					sendDrawerPacket(DrawerInteractToServer.Type.INSERT, cont, slot);
				return true;
			}
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	private static boolean extractItem(ScreenEvent.MouseButtonPressed.Pre event, AbstractContainerScreen<?> cont, @Nullable Slot slot) {
		if (slot == null || !slot.allowModification(Proxy.getClientPlayer())) {
			return false;
		}
		ItemStack stack = cont.getMenu().getCarried();
		ItemStack drawerStack = slot.getItem();
		if (drawerStack.getItem() instanceof BaseDrawerItem drawer && stack.isEmpty()) {
			sendDrawerPacket(DrawerInteractToServer.Type.TAKE, cont, slot);
			return true;
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	private static void sendDrawerPacket(DrawerInteractToServer.Type type, AbstractContainerScreen<?> cont, Slot slot) {
		int index = cont.getMenu().containerId == 0 ? slot.getSlotIndex() : slot.index;
		L2Backpack.HANDLER.toServer(new DrawerInteractToServer(type, cont.getMenu().containerId, index, cont.getMenu().getCarried()));
	}

}
