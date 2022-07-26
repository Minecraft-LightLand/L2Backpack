package dev.xkmc.l2backpack.content.arrowbag;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.common.PlayerSlot;
import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ArrowBag extends BaseBagItem {

	public static List<ItemStack> getItems(ItemStack stack) {
		List<ItemStack> ans = new ArrayList<>();
		ListTag tag = getListTag(stack);
		for (Tag value : tag) {
			ans.add(ItemStack.of((CompoundTag) value));
		}
		return ans;
	}

	public static void setItems(ItemStack stack, List<ItemStack> list) {
		ListTag tag = new ListTag();
		for (int i = 0; i < list.size(); i++) {
			tag.add(i, list.get(i).save(new CompoundTag()));
		}
		ArrowBag.setListTag(stack, tag);
	}

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
		super(props.stacksTo(1));
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
		list.add(LangData.IDS.ARROW_INFO.get());
	}
}
