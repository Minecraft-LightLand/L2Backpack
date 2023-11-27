package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerBlockEntity;
import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerItem;
import dev.xkmc.l2backpack.content.remote.player.EnderSyncCap;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.network.RequestTooltipUpdateEvent;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2library.util.raytrace.RayTraceUtil;
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
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = L2Backpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TooltipUpdateEvents {

	@OnlyIn(Dist.CLIENT)
	public static void onEnderSync(int slot, ItemStack stack) {
		EnderSyncCap.HOLDER.get(Proxy.getClientPlayer()).setItem(slot, stack);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		if (!continueSession()) {
			endSession();
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static boolean continueSession() {
		Screen screen = Minecraft.getInstance().screen;
		if (screen instanceof AbstractContainerScreen<?> cont)
			return screenSession(cont);
		else if (screen == null)
			return blockSession();
		else return false;
	}

	@OnlyIn(Dist.CLIENT)
	private static boolean screenSession(AbstractContainerScreen<?> cont) {
		Slot slot = cont.getSlotUnderMouse();
		if (slot == null) return false;
		ItemStack stack = slot.getItem();
		if (!(stack.getItem() instanceof EnderDrawerItem)) return false;
		if (BaseDrawerItem.getItem(stack) == Items.AIR) return false;
		startSession(BaseDrawerItem.getItem(stack), stack.getOrCreateTag().getUUID(EnderDrawerItem.KEY_OWNER_ID));
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	private static boolean blockSession() {
		LocalPlayer player = Proxy.getClientPlayer();
		var ray = RayTraceUtil.rayTraceBlock(player.level(), player, player.getBlockReach());
		if (ray.getType() == HitResult.Type.BLOCK) {
			BlockPos pos = ray.getBlockPos();
			BlockEntity entity = player.level().getBlockEntity(pos);
			if (entity instanceof EnderDrawerBlockEntity drawer) {
				startSession(drawer.item, drawer.owner_id);
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

	@OnlyIn(Dist.CLIENT)
	private static void startSession(Item content, UUID owner) {
		if (step == Step.NONE) {
			focus = content;
			id = owner;
			step = Step.SENT;
			L2Backpack.HANDLER.toServer(new RequestTooltipUpdateEvent(focus, Proxy.getClientPlayer().getUUID()));
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
