package dev.xkmc.l2backpack.content.backpack;

import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnderBackpackItem extends Item {

	@OnlyIn(Dist.CLIENT)
	public static float isOpened(ItemStack stack, ClientLevel level, LivingEntity entity, int i) {
		if (entity != Proxy.getClientPlayer()) return 0;
		Screen screen = Minecraft.getInstance().screen;
		if (screen instanceof ContainerScreen gui) {
			if (gui.getMenu().getContainer() == Proxy.getClientPlayer().getEnderChestInventory()) {
				return 1;
			}
		}
		return 0;
	}

	public EnderBackpackItem(Properties props) {
		super(props);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			player.openMenu(new SimpleMenuProvider((id, inv, pl) -> ChestMenu.threeRows(id, inv, player.getEnderChestInventory()), stack.getDisplayName()));
		} else {
			player.playSound(SoundEvents.ENDER_CHEST_OPEN, 1, 1);
		}
		return InteractionResultHolder.success(stack);
	}

}
