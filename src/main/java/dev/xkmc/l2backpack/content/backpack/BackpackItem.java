package dev.xkmc.l2backpack.content.backpack;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.common.BackpackModelItem;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.render.BaseItemRenderer;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.BackpackConfig;
import dev.xkmc.l2backpack.init.data.LangData;
import dev.xkmc.l2screentracker.screen.source.PlayerSlot;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class BackpackItem extends BaseBagItem implements BackpackModelItem {

	private static final String ROW = "rows";

	public static final int MAX_ROW = 8;

	public static ItemStack initLootGen(ItemStack stack, ResourceLocation loot) {
		var ctag = stack.getOrCreateTag();
		ctag.putString(LOOT, loot.toString());
		return stack;
	}

	public static ItemStack setRow(ItemStack result, int i) {
		result.getOrCreateTag().putInt(ROW, i);
		return result;
	}

	public final DyeColor color;

	public BackpackItem(DyeColor color, Properties props) {
		super(props.stacksTo(1).fireResistant());
		this.color = color;
	}

	@Override
	public int getRows(ItemStack stack) {
		int ans = Mth.clamp(stack.getOrCreateTag().getInt(ROW),
				BackpackConfig.COMMON.initialRows.get(), MAX_ROW);
		if (!stack.getOrCreateTag().contains(ROW)) {
			stack.getOrCreateTag().putInt(ROW, ans);
		}
		return ans;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		var tag = stack.getOrCreateTag();
		int rows = tag.getInt("rows");
		if (rows == 0) {
			rows = BackpackConfig.COMMON.initialRows.get();
		}
		list.add(LangData.IDS.BACKPACK_SLOT.get(Math.max(1, rows), MAX_ROW));
		if (tag.contains("loot")) {
			list.add(LangData.IDS.LOOT.get().withStyle(ChatFormatting.AQUA));
		} else {
			PickupConfig.addText(stack, list);
		}
		LangData.addInfo(list,
				LangData.Info.QUICK_INV_ACCESS,
				LangData.Info.KEYBIND,
				LangData.Info.UPGRADE,
				LangData.Info.LOAD,
				LangData.Info.EXIT,
				LangData.Info.PICKUP
		);
	}

	@Override
	public void open(ServerPlayer player, PlayerSlot<?> slot, ItemStack stack) {
		new BackpackMenuPvd(player, slot, this, stack).open();
	}

	@Override
	public ResourceLocation getModelTexture(ItemStack stack) {
		return new ResourceLocation(L2Backpack.MODID, "textures/block/backpack/" + color.getName() + ".png");
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(BaseItemRenderer.EXTENSIONS);
	}

}
