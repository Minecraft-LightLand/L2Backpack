package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerBlockEntity;
import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerItem;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2backpack.init.registrate.LBMisc;
import dev.xkmc.l2backpack.network.RequestTooltipUpdateEvent;
import dev.xkmc.l2core.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.UUID;

@EventBusSubscriber(value = Dist.CLIENT, modid = L2Backpack.MODID, bus = EventBusSubscriber.Bus.GAME)
public class TooltipUpdateEvents {

	public static void onEnderSync(int slot, ItemStack stack) {
		LBMisc.ENDER_SYNC.type().getOrCreate(Proxy.getClientPlayer()).setItem(slot, stack);
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		if (!continueSession()) {
			endSession();
		}
	}

	private static boolean continueSession() {
		Screen screen = Minecraft.getInstance().screen;
		if (screen instanceof AbstractContainerScreen<?> cont)
			return screenSession(cont);
		else if (screen == null)
			return blockSession();
		else return false;
	}

	private static boolean screenSession(AbstractContainerScreen<?> cont) {
		Slot slot = cont.getSlotUnderMouse();
		if (slot == null) return false;
		ItemStack stack = slot.getItem();
		if (!(stack.getItem() instanceof EnderDrawerItem)) return false;
		if (EnderDrawerItem.getItem(stack) == Items.AIR) return false;
		var id = LBItems.DC_OWNER_ID.get(stack);
		if (id == null) return false;
		startSession(EnderDrawerItem.getItem(stack), id);
		return true;
	}

	private static boolean blockSession() {
		LocalPlayer player = Proxy.getClientPlayer();
		var ray = Minecraft.getInstance().hitResult;
		if (ray instanceof BlockHitResult bray) {
			BlockPos pos = bray.getBlockPos();
			BlockEntity entity = player.level().getBlockEntity(pos);
			if (entity instanceof EnderDrawerBlockEntity drawer) {
				startSession(drawer.item, drawer.ownerId);
				return true;
			}
		}
		return false;
	}

	private enum Step {
		NONE, SENT, COOLDOWN
	}

	private static final int MAX_COOLDOWN = 5;

	private static Step step = Step.NONE;
	private static UUID id = null;
	private static Item focus = null;
	private static int count = 0;
	private static int cooldown = 0;

	private static void endSession() {
		step = Step.NONE;
		focus = null;
		count = 0;
		id = null;
	}

	private static void startSession(Item content, UUID owner) {
		if (step == Step.NONE) {
			focus = content;
			id = owner;
			step = Step.SENT;
			L2Backpack.HANDLER.toServer(new RequestTooltipUpdateEvent(focus, owner));
		} else if (step == Step.COOLDOWN) {
			if (cooldown > 0) {
				cooldown--;
			}
			if (cooldown <= 0) {
				cooldown = 0;
				step = Step.NONE;
			}
		}
	}

	public static void updateInfo(Item item, UUID uuid, int val) {
		if (focus != item) return;
		if (step != Step.SENT) return;
		count = val;
		id = uuid;
		step = Step.COOLDOWN;
		cooldown = MAX_COOLDOWN;
	}

	public static int getCount(UUID uuid, Item item) {
		if (id != null && focus != null && id.equals(uuid) && item == focus) {
			return count;
		}
		return -1;
	}

}
