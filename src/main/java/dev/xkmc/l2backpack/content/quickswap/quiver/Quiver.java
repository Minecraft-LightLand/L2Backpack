package dev.xkmc.l2backpack.content.quickswap.quiver;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.client.ItemOnBackItem;
import dev.xkmc.l2backpack.content.quickswap.common.IQuickSwapToken;
import dev.xkmc.l2backpack.content.quickswap.common.SimpleMenuPvd;
import dev.xkmc.l2backpack.content.quickswap.common.SingleSwapItem;
import dev.xkmc.l2backpack.content.quickswap.common.SingleSwapToken;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapType;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2backpack.init.data.LBLang;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Quiver extends SingleSwapItem implements ItemOnBackItem {

	public static float displayArrow(ItemStack stack) {
		int disp = 0;
		var cont = LBItems.BACKPACK_CONTENT.get(stack);
		if (cont != null) {
			for (ItemStack arrow : cont.nonEmptyItems()) {
				if (!arrow.isEmpty()) {
					disp++;
				}
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
	public void open(ServerPlayer player, PlayerSlot<?> slot, ItemStack stack) {
		new SimpleMenuPvd(player, slot, this, stack, QuiverMenu::new).open();
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		PickupConfig.addText(stack, list);
		LBLang.addInfo(flag, list,
				LBLang.Info.ARROW_INFO,
				LBLang.Info.INHERIT);
	}

	@Nullable
	@Override
	public IQuickSwapToken<?> getTokenOfType(ItemStack stack, LivingEntity player, QuickSwapType type) {
		if (type != QuickSwapTypes.ARROW)
			return null;
		if (!(player.getMainHandItem().getItem() instanceof ProjectileWeaponItem bow))
			return null;
		var cont = LBItems.BACKPACK_CONTENT.get(stack);
		if (cont == null) return null;
		for (ItemStack arrow : cont.nonEmptyItems()) {
			if (bow.getAllSupportedProjectiles().test(arrow))
				return new SingleSwapToken(this, stack, QuickSwapTypes.ARROW);
		}
		return null;
	}

	@Override
	public boolean isValidContent(ItemStack stack) {
		return isValidStack(stack);
	}
}
