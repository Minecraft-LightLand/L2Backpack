package dev.xkmc.l2backpack.content.capability;

import dev.xkmc.l2library.util.nbt.ItemCompoundTag;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record PickupConfig(PickupMode pickup, DestroyMode destroy) {
	private static final String KEY_ROOT = "backpack_pickup_config";
	private static final String KEY_MODE = "mode";
	private static final String KEY_VOID = "void";

	public static PickupConfig getPickupMode(ItemStack stack) {
		var tag = ItemCompoundTag.of(stack).getSubTag(KEY_ROOT);
		PickupMode mode = PickupMode.NONE;
		DestroyMode destroy = DestroyMode.NONE;
		if (tag.isPresent()) {
			var ctag = tag.getOrCreate();
			if (ctag.contains(KEY_MODE)) {
				String str = ctag.getString(KEY_MODE);
				mode = PickupMode.valueOf(str);
			}
			if (ctag.contains(KEY_VOID)) {
				String str = ctag.getString(KEY_VOID);
				destroy = DestroyMode.valueOf(str);
			}
		}
		return new PickupConfig(mode, destroy);
	}

	public static CompoundTag getConfig(ItemStack stack) {
		return ItemCompoundTag.of(stack).getSubTag(KEY_ROOT).getOrCreate();
	}

	public static void setConfig(ItemStack stack, @Nullable CompoundTag config) {
		if (config == null) return;
		ItemCompoundTag.of(stack).getSubTag(KEY_ROOT).setTag(config);
	}

	public static void addText(ItemStack stack, List<Component> list) {
		var mode = getPickupMode(stack);
		list.add(mode.pickup().getTooltip());
		list.add(mode.destroy().getTooltip());
	}

	public static void iterateMode(ItemStack stack) {
		PickupConfig mode = getPickupMode(stack);
		PickupMode next = PickupMode.values()[(mode.pickup().ordinal() + 1) % PickupMode.values().length];
		getConfig(stack).putString(KEY_MODE, next.name());
	}

	public static void iterateDestroy(ItemStack stack) {
		PickupConfig mode = getPickupMode(stack);
		DestroyMode next = DestroyMode.values()[(mode.destroy().ordinal() + 1) % DestroyMode.values().length];
		getConfig(stack).putString(KEY_VOID, next.name());
	}

}
