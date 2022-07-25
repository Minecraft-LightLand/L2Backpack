package dev.xkmc.l2backpack.content.backpack;

import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BackpackItem extends Item {

	public final DyeColor color;

	public BackpackItem(DyeColor color, Properties props) {
		super(props);
		this.color = color;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			new BackpackMenuPvd((ServerPlayer) player, hand, stack).open();
		} else {
			player.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 1, 1);
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		list.add(LangData.IDS.BACKPACK_SLOT.get(Math.max(1, stack.getOrCreateTag().getInt("rows")), 6));
	}

}
