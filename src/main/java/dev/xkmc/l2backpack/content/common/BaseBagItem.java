package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.content.capability.PickupBagItem;
import dev.xkmc.l2backpack.content.insert.InsertOnlyItem;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseBagItem extends Item implements ContentTransfer.Quad, PickupBagItem, InsertOnlyItem, TooltipInvItem {

	public static List<ItemStack> getItems(ItemStack stack) {
		BaseBagItem item = (BaseBagItem) stack.getItem();
		NonNullList<ItemStack> list = NonNullList.withSize(item.getRows(stack) * 9, ItemStack.EMPTY);
		var cont = LBItems.BACKPACK_CONTENT.get(stack);
		if (cont != null) cont.copyInto(list);
		return list;
	}

	public static void setItems(ItemStack stack, List<ItemStack> list) {
		stack.set(LBItems.BACKPACK_CONTENT, ItemContainerContents.fromItems(list));
	}

	public BaseBagItem(Properties props) {
		super(props);
	}

	public static void checkLootGen(ItemStack stack, Player player) {
		if (!(player.level() instanceof ServerLevel sl)) return;
		if (LBItems.BACKPACK_CONTENT.get(stack) != null) return;
		String lootStr = LBItems.DC_LOOT_ID.get(stack);
		if (lootStr == null) return;
		ResourceLocation rl = ResourceLocation.parse(lootStr);
		long seed = LBItems.DC_LOOT_SEED.getOrDefault(stack, 0L);
		stack.remove(LBItems.DC_LOOT_ID);
		stack.remove(LBItems.DC_LOOT_SEED);
		LootTable loottable = sl.getServer().reloadableRegistries()
				.getLootTable(ResourceKey.create(Registries.LOOT_TABLE, rl));
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
		NonNullList<ItemStack> list = NonNullList.withSize(getRows(stack) * 9, ItemStack.EMPTY);
		if (!client && shift && target != null) {
			var cont = LBItems.BACKPACK_CONTENT.get(stack);
			if (cont != null) cont.copyInto(list);
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
	public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
		return armorType == EquipmentSlot.CHEST;
	}

	@Nonnull
	@Override
	public EquipmentSlot getEquipmentSlot(ItemStack stack) {
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
	public int getInvSize(ItemStack stack) {
		return getRows(stack) * 9;
	}

	@Override
	public List<ItemStack> getInvItems(ItemStack stack, Player player) {
		return getItems(stack);
	}

	public void checkInit(ItemStack stack) {
		if (LBItems.DC_CONT_ID.get(stack) == null) {
			stack.set(LBItems.DC_CONT_ID, UUID.randomUUID());
		}
		if (LBItems.BACKPACK_CONTENT.get(stack) == null) {
			stack.set(LBItems.BACKPACK_CONTENT, ItemContainerContents.EMPTY);
		}
	}

}
