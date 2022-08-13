package dev.xkmc.l2backpack.content.backpack;

import dev.xkmc.l2backpack.content.common.BackpackModelItem;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.common.PlayerSlot;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BackpackItem extends BaseBagItem implements BackpackModelItem {

	public final DyeColor color;

	public BackpackItem(DyeColor color, Properties props) {
		super(props.stacksTo(1).fireResistant());
		this.color = color;
	}

	public static ItemStack setRow(ItemStack result, int i) {
		result.getOrCreateTag().putInt("rows", i);
		return result;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		list.add(LangData.IDS.BACKPACK_SLOT.get(Math.max(1, stack.getOrCreateTag().getInt("rows")), 6));
		LangData.addInfo(list,
				LangData.Info.DUMP,
				LangData.Info.LOAD,
				LangData.Info.QUICK_INV_ACCESS,
				LangData.Info.KEYBIND,
				LangData.Info.UPGRADE,
				LangData.Info.EXIT
		);
	}

	@Override
	public void open(ServerPlayer player, PlayerSlot slot, ItemStack stack) {
		new BackpackMenuPvd(player, slot, stack).open();
	}

	@Override
	public ResourceLocation getModelTexture(ItemStack stack) {
		return new ResourceLocation(L2Backpack.MODID, "textures/block/backpack/" + color.getName() + ".png");
	}

}
