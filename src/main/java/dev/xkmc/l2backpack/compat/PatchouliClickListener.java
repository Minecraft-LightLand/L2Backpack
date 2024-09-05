package dev.xkmc.l2backpack.compat;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2menustacker.click.ReadOnlyStackClickHandler;
import dev.xkmc.l2menustacker.init.L2MSLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;

public class PatchouliClickListener extends ReadOnlyStackClickHandler {

	public PatchouliClickListener() {
		super(L2Backpack.loc("patchouli"));
	}

	@Override
	protected void handle(ServerPlayer player, ItemStack stack) {
		Book book = ItemModBook.getBook(stack);
		if (book == null)
			return;
		PatchouliAPI.get().openBookGUI(player, book.id);
		SoundEvent sfx = PatchouliSounds.getSound(book.openSound, PatchouliSounds.BOOK_OPEN);
		player.playSound(sfx, 1.0F, (float) (0.7 + Math.random() * 0.4));
	}

	@Override
	public boolean isAllowed(ItemStack stack) {
		return stack.is(PatchouliItems.BOOK);
	}

	@SubscribeEvent
	public static void tooltipEvent(ItemTooltipEvent event) {
		if (event.getItemStack().is(PatchouliItems.BOOK)) {
			event.getToolTip().add(L2MSLangData.QUICK_ACCESS.get().withStyle(ChatFormatting.GRAY));
		}
	}

}
