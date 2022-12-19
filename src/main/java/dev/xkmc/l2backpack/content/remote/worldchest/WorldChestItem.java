package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.content.common.BackpackModelItem;
import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.advancement.BackpackTriggers;
import dev.xkmc.l2backpack.init.data.LangData;
import dev.xkmc.l2backpack.init.registrate.BackpackBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WorldChestItem extends BlockItem implements BackpackModelItem {

	public static Optional<UUID> getOwner(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag != null) {
			if (tag.contains("owner_id")) {
				return Optional.of(tag.getUUID("owner_id"));
			}
		}
		return Optional.empty();
	}

	public final DyeColor color;

	public WorldChestItem(DyeColor color, Properties props) {
		super(BackpackBlocks.WORLD_CHEST.get(), props.stacksTo(1).fireResistant());
		this.color = color;
	}

	void refresh(ItemStack stack, Player player) {
		if (!stack.getOrCreateTag().contains("owner_id")) {
			stack.getOrCreateTag().putUUID("owner_id", player.getUUID());
			stack.getOrCreateTag().putString("owner_name", player.getName().getString());
			stack.getOrCreateTag().putLong("password", color.getId());
		}
	}

	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
		if (super.allowedIn(tab)) {
			list.add(new ItemStack(this));
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			new WorldChestMenuPvd((ServerPlayer) player, stack, this).open();
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
				new WorldChestMenuPvd((ServerPlayer) context.getPlayer(), stack, this).open();
			} else {
				ContentTransfer.playSound(context.getPlayer());
			}
			return InteractionResult.SUCCESS;
		}
		if (!context.getItemInHand().getOrCreateTag().contains("owner_id"))
			return InteractionResult.FAIL;
		return super.useOn(context);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		CompoundTag tag = stack.getTag();
		if (tag != null) {
			if (tag.contains("owner_name")) {
				String name = tag.getString("owner_name");
				list.add(LangData.IDS.STORAGE_OWNER.get(name));
			}
		}
		LangData.addInfo(list, LangData.Info.QUICK_ANY_ACCESS,
				LangData.Info.PLACE,
				LangData.Info.DIMENSIONAL,
				LangData.Info.KEYBIND,
				LangData.Info.EXIT);
	}

	public String getDescriptionId() {
		return this.getOrCreateDescriptionId();
	}

	@Override
	public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
		return armorType == EquipmentSlot.CHEST;
	}

	@Override
	public ResourceLocation getModelTexture(ItemStack stack) {
		return new ResourceLocation(L2Backpack.MODID, "textures/block/dimensional_storage/" + color.getName() + ".png");
	}

}
