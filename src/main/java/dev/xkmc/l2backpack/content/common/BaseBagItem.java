package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.content.arrowbag.ArrowBag;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseBagItem extends Item {

	public static ListTag getListTag(ItemStack stack) {
		if (stack.getOrCreateTag().contains("Items")) {
			return stack.getOrCreateTag().getList("Items", Tag.TAG_COMPOUND);
		} else {
			return new ListTag();
		}
	}

	public static void setListTag(ItemStack stack, ListTag list) {
		stack.getOrCreateTag().put("Items", list);
	}

	@OnlyIn(Dist.CLIENT)
	public static float isOpened(ItemStack stack, ClientLevel level, LivingEntity entity, int i) {
		if (entity != Proxy.getClientPlayer()) return 0;
		Screen screen = Minecraft.getInstance().screen;
		if (screen instanceof BaseBagScreen<?> gui) {
			return gui.getMenu().getStack() == stack ? 1 : 0;
		}
		return 0;
	}

	public BaseBagItem(Properties props) {
		super(props);
	}

	public static List<ItemStack> getItems(ItemStack stack) {
		List<ItemStack> ans = new ArrayList<>();
		ListTag tag = getListTag(stack);
		for (Tag value : tag) {
			ans.add(ItemStack.of((CompoundTag) value));
		}
		return ans;
	}

	public static void setItems(ItemStack stack, List<ItemStack> list) {
		ListTag tag = new ListTag();
		for (int i = 0; i < list.size(); i++) {
			tag.add(i, list.get(i).save(new CompoundTag()));
		}
		ArrowBag.setListTag(stack, tag);
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
			open((ServerPlayer) player, PlayerSlot.ofInventory(slot), stack);
		} else {
			player.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 1, 1);
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player != null && player.isShiftKeyDown()) {
			BlockPos pos = context.getClickedPos();
			BlockEntity target = context.getLevel().getBlockEntity(pos);
			if (target != null) {
				var capLazy = target.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
				if (capLazy.resolve().isPresent()) {
					var cap = capLazy.resolve().get();
					if (!context.getLevel().isClientSide()) {
						var list = getItems(context.getItemInHand());
						ContentTransfer.transfer(list, cap);
						setItems(context.getItemInHand(), list);
					}
					return InteractionResult.SUCCESS;
				}
			}
			return InteractionResult.FAIL;
		}
		return super.useOn(context);
	}

	public abstract void open(ServerPlayer player, PlayerSlot slot, ItemStack stack);


}