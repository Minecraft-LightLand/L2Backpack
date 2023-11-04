package dev.xkmc.l2backpack.events;

import com.mojang.blaze3d.platform.InputConstants;
import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.capability.PickupBagItem;
import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.insert.OverlayInsertItem;
import dev.xkmc.l2backpack.content.tool.IBagTool;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.BackpackKeys;
import dev.xkmc.l2backpack.network.CreativeSetCarryToServer;
import dev.xkmc.l2backpack.network.DrawerInteractToServer;
import dev.xkmc.l2itemselector.events.GenericKeyEvent;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = L2Backpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEventHandler {

	@SubscribeEvent
	public static void keyEvent(GenericKeyEvent event) {
		if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292)) {
			return;
		}
		if (Minecraft.getInstance().screen == null &&
				Proxy.getClientPlayer() != null &&
				event.test(BackpackKeys.OPEN.map.getKey()) &&
				event.getAction() == InputConstants.PRESS) {
			if (BackpackSlotClickListener.canOpen(Proxy.getClientPlayer().getItemBySlot(EquipmentSlot.CHEST)) ||
					CuriosCompat.getSlot(Proxy.getClientPlayer(), BackpackSlotClickListener::canOpen).isPresent())
				L2Backpack.SLOT_CLICK.keyBind();
		}
	}

	@SubscribeEvent
	public static void onScreenLeftClick(ScreenEvent.MouseButtonReleased.Pre event) {
		if (event.getScreen() instanceof AbstractContainerScreen<?> scr &&
				scr.getMenu().getCarried().getItem() instanceof IBagTool) {
			var slot = scr.getSlotUnderMouse();
			if (slot != null && slot.getItem().getItem() instanceof PickupBagItem) {
				if (scr instanceof CreativeModeInventoryScreen)
					L2Backpack.HANDLER.toServer(new CreativeSetCarryToServer(ItemStack.EMPTY));
				event.setCanceled(true);
			}
			return;
		}
		if (onRelease(event)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onScreenRightClick(ScreenEvent.MouseButtonPressed.Pre event) {
		if (event.getScreen() instanceof AbstractContainerScreen<?> scr &&
				scr.getMenu().getCarried().getItem() instanceof IBagTool) {
			var slot = scr.getSlotUnderMouse();
			if (slot != null && slot.getItem().getItem() instanceof PickupBagItem) {
				if (scr instanceof CreativeModeInventoryScreen)
					L2Backpack.HANDLER.toServer(new CreativeSetCarryToServer(scr.getMenu().getCarried()));
			}
			return;
		}
		if (onPress(event)) {
			event.setCanceled(true);
		}
	}

	private static boolean onRelease(ScreenEvent.MouseButtonReleased.Pre event) {
		Screen screen = event.getScreen();
		if (screen instanceof AbstractContainerScreen cont) {
			Slot slot = cont.getSlotUnderMouse();
			ItemStack carried = cont.getMenu().getCarried();
			boolean bypass = !carried.isEmpty() &&
					slot != null && slot.getItem().getItem() instanceof OverlayInsertItem item &&
					!item.mayClientTake();
			if (bypass || event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				return insertItem(event, cont, slot, true, event.getButton());
			}
			if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && slot != null) {
				return slot.getItem().getItem() instanceof BaseDrawerItem &&
						!cont.getMenu().getCarried().isEmpty();
			}
		}
		return false;
	}

	private static boolean onPress(ScreenEvent.MouseButtonPressed.Pre event) {
		Screen screen = event.getScreen();
		if (screen instanceof AbstractContainerScreen cont) {
			Slot slot = cont.getSlotUnderMouse();
			ItemStack carried = cont.getMenu().getCarried();
			boolean bypass = !carried.isEmpty() &&
					slot != null && slot.getItem().getItem() instanceof OverlayInsertItem item &&
					!item.mayClientTake();
			if (bypass || event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				return insertItem(event, cont, slot, false, event.getButton());
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

	private static boolean insertItem(ScreenEvent event, AbstractContainerScreen<?> cont, @Nullable Slot slot, boolean perform, int button) {
		if (slot == null || !slot.allowModification(Proxy.getClientPlayer())) {
			return false;
		}
		ItemStack storage = slot.getItem();
		ItemStack carried = cont.getMenu().getCarried();
		if (storage.getItem() instanceof OverlayInsertItem drawer) {
			return drawer.clientInsert(storage, carried, cont.getMenu().containerId, slot, perform, button);
		}
		return false;
	}

	private static boolean extractItem(ScreenEvent.MouseButtonPressed.Pre event, AbstractContainerScreen<?> cont, @Nullable Slot slot) {
		if (slot == null || !slot.allowModification(Proxy.getClientPlayer())) {
			return false;
		}
		ItemStack stack = cont.getMenu().getCarried();
		ItemStack drawerStack = slot.getItem();
		if (drawerStack.getItem() instanceof OverlayInsertItem drawer && drawer.mayClientTake() && stack.isEmpty()) {
			sendDrawerPacket(DrawerInteractToServer.Type.TAKE, cont, slot);
			return true;
		}
		return false;
	}

	private static void sendDrawerPacket(DrawerInteractToServer.Type type, AbstractContainerScreen<?> cont, Slot slot) {
		int index = cont.getMenu().containerId == 0 ? slot.getSlotIndex() : slot.index;
		L2Backpack.HANDLER.toServer(new DrawerInteractToServer(type, cont.getMenu().containerId, index, cont.getMenu().getCarried()));
	}

}
