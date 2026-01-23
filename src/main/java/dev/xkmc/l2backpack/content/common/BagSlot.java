package dev.xkmc.l2backpack.content.common;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BagSlot extends SlotItemHandler {

	private final IItemHandlerModifiable bag;
	private final int index;

	public BagSlot(IItemHandlerModifiable itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
		bag = itemHandler;
		this.index = index;
	}

	private ItemStack session = null;

	public void startSession(ItemStack stack) {
		session = stack;
	}

	public void endSession() {
		if (session != null) {
			set(session);
		}
	}

	@Override
	public void set(ItemStack stack) {
		session = null;
		super.set(stack);
	}

	@Override
	public ItemStack remove(int amount) {
		session = null;
		return super.remove(amount);
	}

	@Override
	public ItemStack getItem() {
		if (session != null) return session;
		return super.getItem();
	}

}
