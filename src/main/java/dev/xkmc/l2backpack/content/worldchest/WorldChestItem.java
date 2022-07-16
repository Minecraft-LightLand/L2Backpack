package dev.xkmc.l2backpack.content.worldchest;

import dev.xkmc.l2backpack.init.data.LangData;
import dev.xkmc.l2backpack.init.registrate.BackpackBlocks;
import dev.xkmc.l2library.util.annotation.ServerOnly;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WorldChestItem extends BlockItem {

	public record MenuPvd(ServerPlayer player, ItemStack stack, WorldChestItem item) implements MenuProvider {

		@Override
		public Component getDisplayName() {
			return stack.getDisplayName();
		}

		@ServerOnly
		@Override
		public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
			StorageContainer container = getContainer((ServerLevel) player.level).get();
			return new WorldChestContainer(id, inventory, container.container, container);
		}

		@ServerOnly
		private Optional<StorageContainer> getContainer(ServerLevel level) {
			CompoundTag tag = stack.getOrCreateTag();
			UUID id = tag.getUUID("owner_id");
			long pwd = tag.getLong("password");
			return WorldStorage.get(level).getOrCreateStorage(id, item.color.getId(), pwd);
		}

		@ServerOnly
		public void open() {
			item.refresh(stack, player);
			if (player.level.isClientSide() || getContainer((ServerLevel) player.level).isEmpty())
				return;
			NetworkHooks.openScreen(player, this);
		}

	}

	public final DyeColor color;

	public WorldChestItem(DyeColor color, Properties props) {
		super(BackpackBlocks.WORLD_CHEST.get(), props);
		this.color = color;
	}

	void refresh(ItemStack stack, Player player) {
		if (!stack.getOrCreateTag().contains("owner_id")) {
			stack.getOrCreateTag().putUUID("owner_id", player.getUUID());
			stack.getOrCreateTag().putString("owner_name", player.getName().getString());
			stack.getOrCreateTag().putLong("password", color.getId());
		}
	}

	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
		if (super.allowedIn(tab)) {
			list.add(new ItemStack(this));
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			new MenuPvd((ServerPlayer) player, stack, this).open();
		} else {
			player.playSound(SoundEvents.ENDER_CHEST_OPEN, 1, 1);
		}
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!context.getLevel().isClientSide() && context.getPlayer() != null)
			refresh(context.getItemInHand(), context.getPlayer());
		if (context.getPlayer() != null && !context.getPlayer().isCrouching()) {
			ItemStack stack = context.getItemInHand();
			if (!context.getLevel().isClientSide()) {
				new MenuPvd((ServerPlayer) context.getPlayer(), stack, this).open();
			} else {
				context.getPlayer().playSound(SoundEvents.ENDER_CHEST_OPEN, 1, 1);
			}
			return InteractionResult.SUCCESS;
		}
		if (!context.getItemInHand().getOrCreateTag().contains("owner_id"))
			return InteractionResult.FAIL;
		return super.useOn(context);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		CompoundTag tag = stack.getTag();
		if (tag == null) return;
		if (tag.contains("owner_name")) {
			String name = tag.getString("owner_name");
			list.add(LangData.IDS.STORAGE_OWNER.get(name));
		}
	}

	public String getDescriptionId() {
		return this.getOrCreateDescriptionId();
	}

}
