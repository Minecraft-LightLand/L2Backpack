package dev.xkmc.l2backpack.content.arrowbag;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.common.PlayerSlot;
import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArrowBag extends BaseBagItem {

	public static void setSelected(ItemStack stack, int i) {
		int slot = i;
		if (i < 0) {
			slot = getSelected(stack);
			if (i == -1) slot--;
			else slot++;
			slot = (slot + 9) % 9;
		}
		stack.getOrCreateTag().putInt("selected", slot);
	}

	public static int getSelected(ItemStack stack) {
		return Mth.clamp(stack.getOrCreateTag().getInt("selected"), 0, 8);
	}

	public ArrowBag(Properties props) {
		super(props.stacksTo(1).fireResistant());
	}

	public static float displayArrow(ItemStack stack) {
		int disp = 0;
		for (ItemStack arrow : getItems(stack)) {
			if (!arrow.isEmpty()) {
				disp++;
			}
		}
		return disp == 0 ? 0 : (float) (Math.ceil(disp / 3f) + 0.5f);
	}

	@Override
	public void open(ServerPlayer player, PlayerSlot slot, ItemStack stack) {
		new ArrowBagMenuPvd(player, slot, stack).open();
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		LangData.addInfo(list,
				LangData.Info.DUMP,
				LangData.Info.LOAD,
				LangData.Info.QUICK_INV_ACCESS,
				LangData.Info.ARROW_INFO,
				LangData.Info.KEYBIND,
				LangData.Info.EXIT);
	}
}
