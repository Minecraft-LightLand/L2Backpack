package dev.xkmc.l2backpack.content.backpack;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.common.BackpackModelItem;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.LBConfig;
import dev.xkmc.l2backpack.init.data.LBLang;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class BackpackItem extends BaseBagItem implements BackpackModelItem {

	public static final int MAX_ROW = 8;

	public final DyeColor color;

	public BackpackItem(DyeColor color, Properties props) {
		super(props.stacksTo(1).fireResistant());
		this.color = color;
	}

	@Override
	public int getRows(ItemStack stack) {
		int old = LBItems.DC_ROW.getOrDefault(stack, 0);
		int ans = Mth.clamp(old, LBConfig.SERVER.initialRows.get(), MAX_ROW);
		if (old != ans) {
			stack.set(LBItems.DC_ROW, ans);
		}
		return ans;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		list.add(LBLang.IDS.BACKPACK_SLOT.get(getRows(stack), MAX_ROW));
		if (LBItems.DC_LOOT_ID.get(stack) != null) {
			list.add(LBLang.IDS.LOOT.get().withStyle(ChatFormatting.AQUA));
		} else {
			PickupConfig.addText(stack, list);
		}
		LBLang.addInfo(flag, list,
				LBLang.Info.QUICK_INV_ACCESS,
				LBLang.Info.KEYBIND,
				LBLang.Info.UPGRADE,
				LBLang.Info.LOAD,
				LBLang.Info.EXIT,
				LBLang.Info.PICKUP
		);
		LBLang.altInsert(flag, list);
	}

	@Override
	public void open(ServerPlayer player, PlayerSlot<?> slot, ItemStack stack) {
		new BackpackMenuPvd(player, slot, this, stack).open();
	}

	@Override
	public ResourceLocation getModelTexture(ItemStack stack) {
		return L2Backpack.loc("textures/block/backpack/" + color.getName() + ".png");
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}

}
