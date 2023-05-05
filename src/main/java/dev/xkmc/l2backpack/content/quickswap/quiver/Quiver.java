package dev.xkmc.l2backpack.content.quickswap.quiver;

import dev.xkmc.l2backpack.content.common.PlayerSlot;
import dev.xkmc.l2backpack.content.quickswap.common.*;
import dev.xkmc.l2backpack.content.render.ItemOnBackItem;
import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Quiver extends SingleSwapItem implements ItemOnBackItem {

	public static float displayArrow(ItemStack stack) {
		int disp = 0;
		for (ItemStack arrow : getItems(stack)) {
			if (!arrow.isEmpty()) {
				disp++;
			}
		}
		return disp == 0 ? 0 : (float) (Math.ceil(disp / 3f) + 0.5f);
	}

	public static boolean isValidStack(ItemStack stack) {
		return stack.getItem().canFitInsideContainerItems() &&
				stack.getItem() instanceof ArrowItem;
	}

	public Quiver(Properties props) {
		super(props.stacksTo(1).fireResistant());
	}

	@Override
	public void open(ServerPlayer player, PlayerSlot slot, ItemStack stack) {
		new SimpleMenuPvd(player, slot, stack, QuiverContainer::new).open();
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		LangData.addInfo(list,
				LangData.Info.ARROW_INFO,
				LangData.Info.INHERIT);
	}

	@Nullable
	@Override
	public IQuickSwapToken getTokenOfType(ItemStack stack, LivingEntity player, QuickSwapType type) {
		if (type != QuickSwapType.ARROW)
			return null;
		if (!(player.getMainHandItem().getItem() instanceof ProjectileWeaponItem bow))
			return null;
		List<ItemStack> list = getItems(stack);
		if (list.isEmpty()) return null;
		for (ItemStack arrow : list) {
			if (!arrow.isEmpty() && bow.getAllSupportedProjectiles().test(arrow))
				return new SingleSwapToken(this, stack, QuickSwapType.ARROW);
		}
		return null;
	}

	@Override
	public boolean isValidContent(ItemStack stack) {
		return isValidStack(stack);
	}
}
