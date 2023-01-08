package dev.xkmc.l2backpack.events;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapManager;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapOverlay;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapType;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.Keys;
import dev.xkmc.l2backpack.network.SetSelectedToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.function.IntConsumer;

public class ArrowBagEvents {

	private static final ThreadLocal<Pair<ItemStack, IntConsumer>> TEMP = new ThreadLocal<>();

	@SubscribeEvent
	public static void onProjectileSearch(LivingGetProjectileEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!(event.getProjectileWeaponItemStack().getItem() instanceof ProjectileWeaponItem weapon)) return;
		IQuickSwapToken token = QuickSwapManager.getToken(player, false);
		if (token == null) return;
		if (token.type() != QuickSwapType.ARROW) return;
		List<ItemStack> arrows = token.getList();
		var pred = weapon.getAllSupportedProjectiles();
		int selected = token.getSelected();
		ItemStack stack = arrows.get(selected);
		if (pred.test(stack)) {
			TEMP.set(Pair.of(stack, token::shrink));
			event.setProjectileItemStack(stack);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void keyEvent(InputEvent.Key event) {
		if (QuickSwapOverlay.INSTANCE.isScreenOn()) {
			if (event.getKey() == Keys.SWAP.map.getKey().getValue() && event.getAction() == InputConstants.PRESS) {
				L2Backpack.HANDLER.toServer(new SetSelectedToServer(SetSelectedToServer.SWAP));
			} else if (event.getKey() == Keys.UP.map.getKey().getValue() && event.getAction() == InputConstants.PRESS) {
				L2Backpack.HANDLER.toServer(new SetSelectedToServer(SetSelectedToServer.UP));
			} else if (event.getKey() == Keys.DOWN.map.getKey().getValue() && event.getAction() == InputConstants.PRESS) {
				L2Backpack.HANDLER.toServer(new SetSelectedToServer(SetSelectedToServer.DOWN));
			} else if (Minecraft.getInstance().options.keyShift.isDown()) {
				for (int i = 0; i < 9; i++) {
					if (Minecraft.getInstance().options.keyHotbarSlots[i].consumeClick()) {
						L2Backpack.HANDLER.toServer(new SetSelectedToServer(i));
					}
				}
			}
		}
	}

	public static void shrink(ItemStack stack, int count) {
		if (TEMP.get() != null && TEMP.get().getFirst() == stack) {
			TEMP.get().getSecond().accept(count);
		}
	}
}
