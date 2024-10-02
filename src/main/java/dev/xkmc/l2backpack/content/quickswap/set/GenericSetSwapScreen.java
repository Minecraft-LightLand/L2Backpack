package dev.xkmc.l2backpack.content.quickswap.set;

import dev.xkmc.l2backpack.content.common.BaseOpenableScreen;
import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class GenericSetSwapScreen<T extends GenericSetSwapMenu<T>> extends BaseOpenableScreen<T> {

	public static final ResourceLocation DISABLED = L2Backpack.loc("disabled");

	public GenericSetSwapScreen(T cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	protected void renderBg(GuiGraphics g, float pTick, int mx, int my) {
		var sr = getRenderer();
		sr.start(g);
	}

	@Override
	protected void renderSlot(GuiGraphics guiGraphics, Slot slot) {
		if (slot instanceof GenericSetBagSlot s) {
			if (s.isDisabled()) {
				guiGraphics.blitSprite(DISABLED, s.x, s.y, 16, 16);
			}
		}
		super.renderSlot(guiGraphics, slot);
	}

	@Override
	public boolean mouseClicked(double mx, double my, int btn) {
		var r = menu.getLayout().getComp("grid");
		int x = r.x + getGuiLeft();
		int y = r.y + getGuiTop();
		if (mx >= x && my >= y && mx < x + r.w * r.rx && my < y + r.h * r.ry) {
			Slot slot = getSlotUnderMouse();
			if (slot != null && slot.getItem().isEmpty() && menu.getCarried().isEmpty()) {
				click(slot.getContainerSlot());
				return true;
			}
		}
		return super.mouseClicked(mx, my, btn);
	}

}
