package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.content.quickswap.quiver.Quiver;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseBagItem extends Item implements ContentTransfer.Quad {

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

	public static long getTimeStamp(ItemStack stack) {
		return stack.getOrCreateTag().getLong("TimeStamp");
	}

	@OnlyIn(Dist.CLIENT)
	public static float isOpened(ItemStack stack, ClientLevel level, LivingEntity entity, int i) {
		if (entity != Proxy.getClientPlayer()) return 0;
		Screen screen = Minecraft.getInstance().screen;
		if ((screen instanceof BaseOpenableScreen<?> gui) && (gui.getMenu() instanceof BaseBagContainer<?> cont)) {
			return cont.getStack() == stack ? 1 : 0;
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
		Quiver.setListTag(stack, tag);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
			open((ServerPlayer) player, PlayerSlot.ofInventory(slot), stack);
		} else {
			ContentTransfer.playSound(player);
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		return ContentTransfer.blockInteract(context, this);
	}

	@Override
	public void click(Player player, ItemStack stack, boolean client, boolean shift, boolean right, @Nullable IItemHandler target) {
		if (!client && shift && right && target != null) {
			var list = getItems(stack);
			int moved = ContentTransfer.transfer(list, target);
			setItems(stack, list);
			ContentTransfer.onDump(player, moved, stack);
		} else if (client && shift && right && target != null)
			ContentTransfer.playSound(player);

		if (!client && shift && !right && target != null) {
			var list = getItems(stack);
			int moved = ContentTransfer.loadFrom(list, target, player, this::isValidContent);
			setItems(stack, list);
			ContentTransfer.onLoad(player, moved, stack);
		} else if (client && shift && !right && target != null)
			ContentTransfer.playSound(player);
	}

	public boolean isValidContent(ItemStack stack) {
		return stack.getItem().canFitInsideContainerItems();
	}

	public abstract void open(ServerPlayer player, PlayerSlot slot, ItemStack stack);

	@Override
	public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
		return armorType == EquipmentSlot.CHEST;
	}

	@Override
	public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return EquipmentSlot.CHEST;
	}

}
