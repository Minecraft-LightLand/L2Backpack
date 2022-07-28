package dev.xkmc.l2backpack.content.remote.drawer;

import dev.xkmc.l2backpack.content.common.BaseItemRenderer;
import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.remote.DrawerAccess;
import dev.xkmc.l2backpack.events.TooltipUpdateEvents;
import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class EnderDrawerItem extends BlockItem implements BaseDrawerItem {

	public static final String KEY_OWNER_ID = "owner_id";
	public static final String KEY_OWNER_NAME = "owner_name";

	public static final int MAX = 64;

	public EnderDrawerItem(Block block, Properties properties) {
		super(block, properties.stacksTo(1).fireResistant());
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(BaseItemRenderer.EXTENSIONS);
	}

	void refresh(ItemStack drawer, Player player) {
		if (!drawer.getOrCreateTag().contains(KEY_OWNER_ID)) {
			drawer.getOrCreateTag().putUUID(KEY_OWNER_ID, player.getUUID());
			drawer.getOrCreateTag().putString(KEY_OWNER_NAME, player.getName().toString());
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!context.getLevel().isClientSide() && context.getPlayer() != null)
			refresh(context.getItemInHand(), context.getPlayer());
		if (!context.getItemInHand().getOrCreateTag().contains(KEY_OWNER_ID))
			return InteractionResult.FAIL;
		if (BaseDrawerItem.getItem(context.getItemInHand()) == Items.AIR)
			return InteractionResult.FAIL;
		return super.useOn(context);
	}

	@Override
	public void insert(ItemStack drawer, ItemStack stack, Player player) {
		DrawerAccess access = DrawerAccess.of(player, BaseDrawerItem.getItem(drawer));
		int count = access.getCount();
		int take = Math.min(MAX * stack.getMaxStackSize() - count, stack.getCount());
		access.setCount(access.getCount() + take);
		stack.shrink(take);
	}

	@Override
	public ItemStack takeItem(ItemStack drawer, Player player) {
		DrawerAccess access = DrawerAccess.of(player, BaseDrawerItem.getItem(drawer));
		Item item = BaseDrawerItem.getItem(drawer);
		int take = Math.min(access.getCount(), item.getMaxStackSize());
		access.setCount(access.getCount() - take);
		return new ItemStack(item, take);
	}

	@Override
	public boolean canSetNewItem(ItemStack drawer) {
		return BaseDrawerItem.getItem(drawer) == Items.AIR;
	}

	@Override
	public void setItem(ItemStack drawer, Item item, Player player) {
		BaseDrawerItem.super.setItem(drawer, item, player);
		refresh(drawer, player);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		Item item = BaseDrawerItem.getItem(stack);
		if (item != Items.AIR) {
			int count = TooltipUpdateEvents.getCount(item);
			list.add(LangData.IDS.DRAWER_CONTENT.get(item.getDescription(), count < 0 ? "???" : count));
		}
		LangData.addInfo(list, LangData.Info.DRAWER_USE, LangData.Info.PLACE, LangData.Info.ENDER_DRAWER_USE);
	}

	public String getDescriptionId() {
		return this.getOrCreateDescriptionId();
	}

}
