package dev.xkmc.l2backpack.content.tool;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DestroyTweakerTool extends Item implements IBagTool {

	public DestroyTweakerTool(Properties properties) {
		super(properties);
	}

	@Override
	public void click(ItemStack stack) {
		PickupConfig.iterateDestroy(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		LangData.addInfo(list, LangData.Info.DESTROY_TWEAKER);
	}

}
