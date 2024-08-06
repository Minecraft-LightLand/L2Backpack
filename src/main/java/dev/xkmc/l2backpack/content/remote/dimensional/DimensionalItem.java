package dev.xkmc.l2backpack.content.remote.dimensional;

import dev.xkmc.l2backpack.content.capability.PickupBagItem;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.common.BackpackModelItem;
import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.content.insert.InsertOnlyItem;
import dev.xkmc.l2backpack.content.remote.common.LBSavedData;
import dev.xkmc.l2backpack.content.remote.common.StorageContainer;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.LBLang;
import dev.xkmc.l2backpack.init.registrate.LBBlocks;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2core.util.ServerOnly;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DimensionalItem extends BlockItem implements BackpackModelItem, PickupBagItem, InsertOnlyItem {

	public static ItemStack initLootGen(ItemStack stack, UUID uuid, Component name, DyeColor color, ResourceLocation loot, long seed) {
		LBItems.DC_OWNER_ID.set(stack, uuid);
		LBItems.DC_OWNER_NAME.set(stack, name);
		LBItems.DC_PASSWORD.set(stack, (long) color.getId());
		LBItems.DC_LOOT_ID.set(stack, loot.toString());
		LBItems.DC_LOOT_SEED.set(stack, seed);
		return stack;
	}

	public final DyeColor color;

	public DimensionalItem(DyeColor color, Properties props) {
		super(LBBlocks.DIMENSIONAL.get(), props.stacksTo(1).fireResistant());
		this.color = color;
	}

	void refresh(ItemStack stack, Player player) {
		if (LBItems.DC_OWNER_ID.get(stack) == null) {
			LBItems.DC_OWNER_ID.set(stack, player.getUUID());
			LBItems.DC_OWNER_NAME.set(stack, player.getName());
			LBItems.DC_PASSWORD.set(stack, (long) color.getId());
		}
		if (LBItems.DC_LOOT_ID.get(stack) != null) {
			new DimensionalMenuPvd((ServerPlayer) player, stack, this).getContainer((ServerLevel) player.level());
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			new DimensionalMenuPvd((ServerPlayer) player, stack, this).open();
		} else {
			ContentTransfer.playSound(player);
		}
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!context.getLevel().isClientSide() && context.getPlayer() != null)
			refresh(context.getItemInHand(), context.getPlayer());
		if (context.getPlayer() != null && !context.getPlayer().isCrouching()) {
			ItemStack stack = context.getItemInHand();
			if (!context.getLevel().isClientSide()) {
				new DimensionalMenuPvd((ServerPlayer) context.getPlayer(), stack, this).open();
			} else {
				ContentTransfer.playSound(context.getPlayer());
			}
			return InteractionResult.SUCCESS;
		}
		if (LBItems.DC_OWNER_ID.get(context.getItemInHand()) == null)
			return InteractionResult.FAIL;
		return super.useOn(context);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		var name = LBItems.DC_OWNER_NAME.get(stack);
		if (name != null) {
			list.add(LBLang.IDS.STORAGE_OWNER.get(name));
			PickupConfig.addText(stack, list);
		}
		if (LBItems.DC_LOOT_ID.get(stack) != null)
			list.add(LBLang.IDS.LOOT.get().withStyle(ChatFormatting.AQUA));
		LBLang.addInfo(flag, list, LBLang.Info.QUICK_ANY_ACCESS,
				LBLang.Info.PLACE,
				LBLang.Info.DIMENSIONAL,
				LBLang.Info.KEYBIND,
				LBLang.Info.EXIT,
				LBLang.Info.PICKUP);
		LBLang.altInsert(flag, list);
	}

	public String getDescriptionId() {
		return this.getOrCreateDescriptionId();
	}

	@Override
	public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
		return armorType == EquipmentSlot.CHEST;
	}

	@Override
	public ResourceLocation getModelTexture(ItemStack stack) {
		return L2Backpack.loc("textures/block/dimensional_storage/" + color.getName() + ".png");
	}

	@ServerOnly
	public Optional<StorageContainer> getContainer(ItemStack stack, ServerLevel level) {
		var id = LBItems.DC_OWNER_ID.get(stack);
		if (id == null) return Optional.empty();
		long pwd = LBItems.DC_PASSWORD.getOrDefault(stack, 0L);
		return LBSavedData.get(level).getOrCreateStorage(id, color.getId(), pwd, null, null, 0);
	}

	@Override
	public @Nullable IItemHandler getInvCap(ItemStack stack, ServerPlayer player) {
		var opt = getContainer(stack, player.serverLevel());
		if (opt.isPresent()) {
			var storage = opt.get();
			return new DimensionalInvWrapper(storage.get(), storage.id);
		}
		return null;
	}

}
