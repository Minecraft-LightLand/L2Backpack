package dev.xkmc.l2backpack.content.quickswap.wheel;

import dev.xkmc.l2backpack.content.quickswap.common.WheelSelectToServer;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapManager;
import dev.xkmc.l2backpack.content.quickswap.type.QuickSwapTypes;
import dev.xkmc.l2backpack.events.BackpackSel;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2itemselector.init.data.L2ISConfig;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.wheel.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ProjectileWeaponItem;

public class SwapWheelKeyHandler {

	public static final Fast FAST = new Fast();
	public static final Switcher SWITCHER = new Switcher();

	static WheelKeyHandler getDefault() {
		return L2ISConfig.CLIENT.useFastSwitchWheel.get() ? SWITCHER : FAST;
	}

	public static class Fast extends DefaultKeyHandler.Fast {

		@Override
		protected void execute(WheelAdaptor<?> wheel, Player player, ActionCode action, WheelContext ctx) {
			super.execute(wheel, player, action, ctx);
			if (action == ActionCode.SELECT) {
				BackpackSel.clicked = true;
			}
		}

		@Override
		public boolean onReleaseWithWheel(WheelAdaptor<?> wheel, Player player, boolean longPress, boolean heldWithWheel) {
			if (longPress && wheel instanceof SwapWheel<?> sw) {
				int index = getSelect(wheel, player);
				if (index >= 0) {
					var token = sw.token();
					if (BackpackSel.clicked) {
						token.setSelected(index);
						return true;
					}
				}
			}
			return super.onReleaseWithWheel(wheel, player, longPress, heldWithWheel);
		}

	}

	public static class Switcher extends DefaultKeyHandler.Switcher {

		@Override
		public boolean onReleaseWithWheel(WheelAdaptor<?> wheel, Player player, boolean longPress, boolean heldWithWheel) {
			if (longPress && wheel instanceof SwapWheel<?> sw) {
				int index = getSelect(wheel, player);
				if (index >= 0) {
					var token = sw.token();
					token.setSelected(index);
					if (token.type() == QuickSwapTypes.ARROW) {
						wheel.select(index);
					}
				}
				return true;
			}
			return super.onReleaseWithWheel(wheel, player, longPress, heldWithWheel);
		}

		@Override
		public void onReleaseWithoutWheel(WheelAdaptor<?> sel, Player player, boolean longPress) {
			BackpackSel.prevType = null;
			if (!longPress && sel instanceof SwapWheel<?> wheel) {
				var token = wheel.token();
				if (token.type() == QuickSwapTypes.ARROW && player.getMainHandItem().getItem() instanceof ProjectileWeaponItem) {
					var list = QuickSwapManager.getWheelTokens(player, L2Keys.hasShiftDown());
					for (int i = 0; i < list.size(); i++) {
						var t = list.get(i);
						if (t.type() == QuickSwapTypes.TOOL && t.type().supportWheel()) {
							int selected = t.getSelected();
							L2Backpack.HANDLER.toServer(new WheelSelectToServer(selected, i, L2Keys.hasShiftDown()));
							return;
						}
					}
				}
			}
			super.onReleaseWithoutWheel(sel, player, longPress);
		}
	}

}
