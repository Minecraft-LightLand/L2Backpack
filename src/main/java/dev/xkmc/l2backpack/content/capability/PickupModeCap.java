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

public interface PickupModeCap {

	Capability<PickupModeCap> TOKEN = CapabilityManager.get(new CapabilityToken<>() {
	});

	String KEY_ROOT = "backpack_pickup_config", KEY_MODE = "mode", KEY_VOID = "void";

	static void register() {

	}

	static PickupConfig getPickupMode(ItemStack stack) {
		var tag = ItemCompoundTag.of(stack).getSubTag(KEY_ROOT);
		PickupMode mode = PickupMode.NONE;
		boolean destroy = false;
		if (tag.isPresent()) {
			var ctag = tag.getOrCreate();
			if (ctag.contains(KEY_MODE)) {
				String str = ctag.getString(KEY_MODE);
				mode = PickupMode.valueOf(str);
			}
			if (ctag.contains(KEY_VOID)) {
				destroy = ctag.getBoolean(KEY_VOID);
			}
		}
		return new PickupConfig(mode, destroy);
	}

	static CompoundTag getConfig(ItemStack stack) {
		return ItemCompoundTag.of(stack).getSubTag(KEY_ROOT).getOrCreate();
	}

	static void setConfig(ItemStack stack, @Nullable CompoundTag config) {
		if (config == null) return;
		ItemCompoundTag.of(stack).getSubTag(KEY_ROOT).setTag(config);
	}

	static void addText(ItemStack stack, List<Component> list) {
		var mode = getPickupMode(stack);
		list.add(LangData.IDS.MODE_DISPLAY.get(mode.pickup().getTooltip()).withStyle(ChatFormatting.GRAY));
		if (mode.destroy()) {
			list.add(LangData.IDS.MODE_DESTROY.get().withStyle(ChatFormatting.RED));
		}
	}

	static void iterateMode(ItemStack stack) {
		PickupConfig mode = getPickupMode(stack);
		PickupMode next = PickupMode.values()[(mode.pickup().ordinal() + 1) % PickupMode.values().length];
		getConfig(stack).putString(KEY_MODE, next.name());
	}

	static void iterateDestroy(ItemStack stack) {
		PickupConfig mode = getPickupMode(stack);
		getConfig(stack).putBoolean(KEY_VOID, !mode.destroy());
	}

	PickupConfig getPickupMode();

	int doPickup(ItemStack stack, PickupTrace trace);

	int getSignature();

}
