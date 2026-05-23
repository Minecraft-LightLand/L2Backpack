package dev.xkmc.l2backpack.content.remote.player;

import dev.xkmc.l2backpack.content.capability.PickupBagItem;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.common.BackpackModelItem;
import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.content.common.InvTooltip;
import dev.xkmc.l2backpack.content.common.TooltipInvItem;
import dev.xkmc.l2backpack.content.insert.InsertOnlyItem;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapItem;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.LBLang;
import dev.xkmc.l2backpack.init.registrate.LBMisc;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class EnderBackpackItem extends Item implements
		BackpackModelItem, PickupBagItem, InsertOnlyItem, TooltipInvItem, IQuickSwapItem {

	public EnderBackpackItem(Properties props) {
		super(props.stacksTo(1));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			player.openMenu(new SimpleMenuProvider((id, inv, pl) -> ChestMenu.threeRows(id, inv, player.getEnderChestInventory()), stack.getHoverName()));
		} else {
			ContentTransfer.playSound(player);
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		PickupConfig.addText(stack, list);
		LBLang.addInfo(flag, list,
				LBLang.Info.QUICK_ANY_ACCESS,
				LBLang.Info.KEYBIND,
				LBLang.Info.PICKUP);
		LBLang.altInsert(flag, list);
	}

	@Override
	public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
		return armorType == EquipmentSlot.CHEST;
	}

	@Override
	public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return EquipmentSlot.CHEST;
	}

	@Override
	public ResourceLocation getModelTexture(ItemStack stack) {
		return L2Backpack.loc("textures/block/ender_backpack.png");
	}

	@Override
	public @Nullable IItemHandler getInvCap(ItemStack storage, ServerPlayer player) {
		return new InvWrapper(player.getEnderChestInventory());
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return InvTooltip.get(this, stack);
	}

	@Override
	public int getInvSize(ItemStack stack) {
		return 27;
	}

	@Override
	public List<ItemStack> getInvItems(ItemStack stack, Player player) {
		return LBMisc.ENDER_SYNC.type().getOrCreate(player).getItems(player);
	}

	@Nullable
	@Override
	public IQuickSwapToken<?> getTokenOfType(ItemStack stack, LivingEntity entity, QuickSwapType type) {
		return entity instanceof Player player ? LBMisc.ENDER_SYNC.type().getExisting(player)
												 .map(e -> e.getToken(player, type)).orElse(null) : null;
	}

	@Override
	public List<IQuickSwapToken<?>> getAllTokensOfType(ItemStack stack, LivingEntity user, QuickSwapType t) {
		return user instanceof Player player ? LBMisc.ENDER_SYNC.type().getExisting(player)
											   .map(e -> e.getAllTokens(player, t)).orElse(List.of()) : List.of();
	}

}
