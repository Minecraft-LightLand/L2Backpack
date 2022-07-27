package dev.xkmc.l2backpack.content.remote.drawer;

import dev.xkmc.l2backpack.content.common.BaseItemRenderer;
import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.remote.DrawerAccess;
import dev.xkmc.l2backpack.events.TooltipUpdateEvents;
import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class EnderDrawerItem extends BaseDrawerItem {

	public static final String KEY_OWNER = "owner";

	public static final int MAX = 64;

	public EnderDrawerItem(Properties properties) {
		super(properties);
	}

	@Override
	public void insert(ItemStack drawer, ItemStack stack, Player player) {
		DrawerAccess access = DrawerAccess.of(player, getItem(drawer));
		int count = access.getCount();
		int take = Math.min(MAX * stack.getMaxStackSize() - count, stack.getCount());
		access.setCount(access.getCount() + take);
		stack.shrink(take);
	}

	@Override
	public ItemStack takeItem(ItemStack drawer, Player player) {
		DrawerAccess access = DrawerAccess.of(player, getItem(drawer));
		Item item = getItem(drawer);
		int take = Math.min(access.getCount(), item.getMaxStackSize());
		access.setCount(access.getCount() - take);
		return new ItemStack(item, take);
	}

	@Override
	public boolean canSetNewItem(ItemStack drawer) {
		return getItem(drawer) == Items.AIR;
	}

	@Override
	public void setItem(ItemStack drawer, Item item, Player player) {
		super.setItem(drawer, item, player);
		if (!drawer.getOrCreateTag().contains(KEY_OWNER)) {
			drawer.getOrCreateTag().putString(KEY_OWNER, player.getUUID().toString());
		}
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(BaseItemRenderer.EXTENSIONS);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		Item item = getItem(stack);
		if (item != Items.AIR) {
			int count = TooltipUpdateEvents.getCount(item);
			list.add(LangData.IDS.DRAWER_CONTENT.get(item.getDescription(), count < 0 ? "???" : count));
		}
		list.add(LangData.IDS.DRAWER_INFO.get());
	}
}
