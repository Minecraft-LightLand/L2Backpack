package dev.xkmc.l2backpack.content.quickswap.merged;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.content.common.PlayerSlot;
import dev.xkmc.l2backpack.content.quickswap.armorswap.ArmorSwap;
import dev.xkmc.l2backpack.content.quickswap.common.*;
import dev.xkmc.l2backpack.content.quickswap.quiver.Quiver;
import dev.xkmc.l2backpack.content.quickswap.scabbard.Scabbard;
import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MultiSwitch extends BaseBagItem implements IQuickSwapItem {

	public MultiSwitch(Properties props) {
		super(props.stacksTo(1).fireResistant());
	}

	@Override
	public void open(ServerPlayer player, PlayerSlot slot, ItemStack stack) {
		new SimpleMenuPvd(player, slot, stack, MultiSwitchContainer::new).open();
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		LangData.addInfo(list,
				LangData.Info.ARROW_INFO,
				LangData.Info.SCABBARD_INFO,
				LangData.Info.ARMORBAG_INFO,
				LangData.Info.QUICK_INV_ACCESS,
				LangData.Info.KEYBIND,
				LangData.Info.DUMP,
				LangData.Info.LOAD,
				LangData.Info.EXIT);
	}

	@Nullable
	@Override
	public IQuickSwapToken getTokenOfType(ItemStack stack, Player player, QuickSwapType type) {
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
		return true;
	}

	@Override
	public void click(Player player, ItemStack stack, boolean client, boolean shift, boolean right, @Nullable IItemHandler target) {
		if (!client && shift && right && target != null) {
			var list = getItems(stack);
			int moved = ContentTransfer.transfer(list, target);
			setItems(stack, list);
			ContentTransfer.onDump(player, moved);
		} else if (client && shift && right && target != null)
			ContentTransfer.playSound(player);

		if (!client && shift && !right && target != null) {
			var list = getItems(stack);
			var list_a = list.subList(0, 9);
			var list_b = list.subList(9, 18);
			var list_c = list.subList(18, 27);
			int moved_a = ContentTransfer.loadFrom(list_a, target, player, Quiver::isValidStack);
			int moved_b = ContentTransfer.loadFrom(list_b, target, player, Scabbard::isValidItem);
			int moved_c = ContentTransfer.loadFrom(list_c, target, player, ArmorSwap::isValidItem);
			int moved = moved_a + moved_b + moved_c;
			setItems(stack, list);
			ContentTransfer.onLoad(player, moved);
		} else if (client && shift && !right && target != null)
			ContentTransfer.playSound(player);
	}

}
