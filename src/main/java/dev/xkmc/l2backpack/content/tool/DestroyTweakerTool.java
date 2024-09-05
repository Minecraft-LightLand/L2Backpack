package dev.xkmc.l2backpack.content.tool;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.init.data.LBLang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class DestroyTweakerTool extends TweakerTool {

	public DestroyTweakerTool(Properties properties) {
		super(properties);
	}

	@Override
	public void click(ItemStack stack) {
		PickupConfig.iterateDestroy(stack);
	}

	@Override
	public PickupConfig click(PickupConfig config) {
		return config.iterateDestroy();
	}

	@Override
	public Component message(PickupConfig config) {
		return config.destroy().getTooltip();
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		LBLang.addInfo(flag, list,
				LBLang.Info.DESTROY_TWEAKER,
				LBLang.Info.TWEAKER_BACK,
				LBLang.Info.TWEAKER_BLOCK
		);
	}

}
