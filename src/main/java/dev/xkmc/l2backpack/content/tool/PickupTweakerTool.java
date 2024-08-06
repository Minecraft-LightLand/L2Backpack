package dev.xkmc.l2backpack.content.tool;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.init.data.LBLang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class PickupTweakerTool extends TweakerTool {

	public PickupTweakerTool(Properties properties) {
		super(properties);
	}

	@Override
	public void click(ItemStack stack) {
		PickupConfig.iterateMode(stack);
	}

	@Override
	public PickupConfig click(PickupConfig config) {
		return config.iterateMode();
	}

	@Override
	public Component message(PickupConfig config) {
		return config.pickup().getTooltip();
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		LBLang.addInfo(flag, list,
				LBLang.Info.PICKUP_TWEAKER,
				LBLang.Info.TWEAKER_BACK,
				LBLang.Info.TWEAKER_BLOCK
		);
	}

}
