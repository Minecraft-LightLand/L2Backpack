package dev.xkmc.l2backpack.content.capability;

import dev.xkmc.l2backpack.init.data.LangData;
import dev.xkmc.l2library.util.nbt.ItemCompoundTag;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface BackpackCap {

	Capability<BackpackCap> TOKEN = CapabilityManager.get(new CapabilityToken<>() {
	});

	String KEY_ROOT = "backpack_pickup_config", KEY_MODE = "mode";

	static void register() {

	}

	static PickupMode getMode(ItemStack stack) {
		var tag = ItemCompoundTag.of(stack).getSubTag(KEY_ROOT);
		if (tag.isPresent()) {
			var ctag = tag.getOrCreate();
			if (ctag.contains(KEY_MODE)) {
				String str = ctag.getString(KEY_MODE);
				return PickupMode.valueOf(str);
			}
		}
		return PickupMode.NONE;
	}

	static CompoundTag getConfig(ItemStack stack) {
		return ItemCompoundTag.of(stack).getSubTag(KEY_ROOT).getOrCreate();
	}

	static void setConfig(ItemStack stack, @Nullable CompoundTag config) {
		if (config == null) return;
		ItemCompoundTag.of(stack).getSubTag(KEY_ROOT).setTag(config);
	}

	static void addText(ItemStack stack, List<Component> list) {
		var mode = getMode(stack);
		list.add(LangData.IDS.MODE_DISPLAY.get(mode.getTooltip()).withStyle(ChatFormatting.GRAY));
	}

	static void iterateMode(ItemStack stack) {
		PickupMode mode = getMode(stack);
		PickupMode next = PickupMode.values()[(mode.ordinal() + 1) % PickupMode.values().length];
		getConfig(stack).putString(KEY_MODE, next.name());
	}

	PickupMode getMode();

	int doPickup(ItemStack stack, PickupTrace trace);

	int getSignature();

}
