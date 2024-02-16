package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.content.capability.PickupBagItem;
import dev.xkmc.l2backpack.content.insert.InsertOnlyItem;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2screentracker.screen.source.PlayerSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseBagItem extends Item implements ContentTransfer.Quad, PickupBagItem, InsertOnlyItem, TooltipInvItem {

	protected static final String LOOT = "loot";
	protected static final String SEED = "seed";

	public static ListTag getListTag(ItemStack stack) {
		var tag = stack.getOrCreateTag();
		if (tag.contains("Items")) {
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
		if ((screen instanceof BaseOpenableScreen<?> gui) && (gui.getMenu() instanceof BaseBagMenu<?> cont)) {
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
			if (value instanceof CompoundTag ctag) {
				ItemStack i = ItemStack.of(ctag);
				int count = ctag.getInt("Count");
				if (i.getCount() < count) {
					i.setCount(count);
				}
				ans.add(i);
			}
		}
		if (!ans.isEmpty()) {
			int size = ((BaseBagItem) stack.getItem()).getRows(stack) * 9;
			while (ans.size() < size) {
				ans.add(ItemStack.EMPTY);
			}
		}
		return ans;
	}

	public static void setItems(ItemStack stack, List<ItemStack> list) {
		ListTag tag = new ListTag();
		for (int i = 0; i < list.size(); i++) {
			tag.add(i, list.get(i).save(new CompoundTag()));
		}
		BaseBagItem.setListTag(stack, tag);
	}

	public static void checkLootGen(ItemStack stack, Player player) {
		if (!getListTag(stack).isEmpty()) return;
		CompoundTag ctag = stack.getOrCreateTag();
		if (!ctag.contains(LOOT)) return;
		ResourceLocation rl = new ResourceLocation(ctag.getString(LOOT));
		long seed = ctag.getLong(SEED);
		ctag.remove(LOOT);
		ctag.remove(SEED);
		if (!(player.level() instanceof ServerLevel sl)) return;
		LootTable loottable = sl.getServer().getLootData().getLootTable(rl);
		LootParams.Builder builder = new LootParams.Builder(sl);
		builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
		BaseBagItem bag = (BaseBagItem) stack.getItem();
		Container cont = new SimpleContainer(bag.getRows(stack));
		loottable.fill(cont, builder.create(LootContextParamSets.EMPTY), seed);
		List<ItemStack> list = new ArrayList<>();
		for (int i = 0; i < cont.getContainerSize(); i++) {
			list.add(cont.getItem(i));
		}
		setItems(stack, list);
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
		List<ItemStack> list = null;
		if (!client && shift && target != null) {
			list = getItems(stack);
			int slot = getRows(stack) * 9;
			while (list.size() < slot) {
				list.add(ItemStack.EMPTY);
			}
		}
		if (!client && shift && right && target != null) {
			int moved = ContentTransfer.transfer(list, target);
			setItems(stack, list);
			ContentTransfer.onDump(player, moved, stack);
		} else if (client && shift && right && target != null) {
			ContentTransfer.playSound(player);
		}
		if (!client && shift && !right && target != null) {
			int moved = ContentTransfer.loadFrom(list, target, player, this::isValidContent);
			setItems(stack, list);
			ContentTransfer.onLoad(player, moved, stack);
		} else if (client && shift && !right && target != null) {
			ContentTransfer.playSound(player);
		}
	}

	public boolean isValidContent(ItemStack stack) {
		return stack.getItem().canFitInsideContainerItems();
	}

	public abstract void open(ServerPlayer player, PlayerSlot<?> slot, ItemStack stack);

	@Override
	public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
		return armorType == EquipmentSlot.CHEST;
	}

	@Override
	public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return EquipmentSlot.CHEST;
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return InvTooltip.get(this, stack);
	}

	public int getRows(ItemStack stack) {
		return 1;
	}

	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return isValidContent(stack);
	}

	@Override
	public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new BaseBagInvWrapper(stack);
	}

	@Override
	public int getInvSize(ItemStack stack) {
		return BaseBagItem.getItems(stack).size();
	}

	@Override
	public List<ItemStack> getInvItems(ItemStack stack, Player player) {
		return BaseBagItem.getItems(stack);
	}

	public void checkInit(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.getBoolean("init")) {
			tag.putBoolean("init", true);
			tag.putUUID("container_id", UUID.randomUUID());
			if (!tag.contains("Items")) {
				var list = getItems(stack);
				int size = getRows(stack) * 9;
				while (list.size() < size) {
					list.add(ItemStack.EMPTY);
				}
				setItems(stack, list);
			}
		}
	}

}
