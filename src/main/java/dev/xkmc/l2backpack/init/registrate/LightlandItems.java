package dev.xkmc.l2backpack.init.registrate;

import dev.xkmc.l2backpack.init.data.ItemTags;
import dev.xkmc.l2library.repack.registrate.providers.DataGenContext;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateItemModelProvider;
import dev.xkmc.l2library.repack.registrate.util.entry.ItemEntry;
import dev.xkmc.l2backpack.content.item.BackpackItem;
import dev.xkmc.l2backpack.content.item.EnderBackpackItem;
import dev.xkmc.l2backpack.content.item.WorldChestItem;
import dev.xkmc.l2backpack.init.LightLand;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.function.Supplier;

import static dev.xkmc.l2backpack.init.LightLand.REGISTRATE;

@SuppressWarnings({"rawtypes", "unchecked", "unsafe"})
@MethodsReturnNonnullByDefault
public class LightlandItems {

	public static class Tab extends CreativeModeTab {

		private final Supplier<ItemEntry> icon;

		public Tab(String id, Supplier<ItemEntry> icon) {
			super(LightLand.MODID + "." + id);
			this.icon = icon;
		}

		@Override
		public ItemStack makeIcon() {
			return icon.get().asStack();
		}
	}

	public static final Tab TAB_MAIN = new Tab("backpack", () -> LightlandItems.ENDER_BACKPACK);

	static {
		REGISTRATE.creativeModeTab(() -> TAB_MAIN);
	}

	// -------- common --------
	public static final ItemEntry<BackpackItem>[] BACKPACKS;
	public static final ItemEntry<WorldChestItem>[] DIMENSIONAL_STORAGE;
	public static final ItemEntry<EnderBackpackItem> ENDER_BACKPACK;
	public static final ItemEntry<Item> ENDER_POCKET;


	static {
		// Backpacks
		{
			BACKPACKS = new ItemEntry[16];
			for (int i = 0; i < 16; i++) {
				DyeColor color = DyeColor.values()[i];
				BACKPACKS[i] = REGISTRATE.item("backpack_" + color.getName(), p -> new BackpackItem(color, p.stacksTo(1)))
						.tag(ItemTags.BACKPACKS.tag).model(LightlandItems::createBackpackModel)
						.color(() -> () -> (stack, val) -> val == 0 ? -1 : ((BackpackItem) stack.getItem()).color.getMaterialColor().col)
						.defaultLang().register();
			}
			DIMENSIONAL_STORAGE = new ItemEntry[16];
			for (int i = 0; i < 16; i++) {
				DyeColor color = DyeColor.values()[i];
				DIMENSIONAL_STORAGE[i] = REGISTRATE.item("dimensional_storage_" + color.getName(), p -> new WorldChestItem(color, p.stacksTo(1)))
						.tag(ItemTags.DIMENSIONAL_STORAGES.tag).model(LightlandItems::createWorldChestModel)
						.color(() -> () -> (stack, val) -> val == 0 ? -1 : ((WorldChestItem) stack.getItem()).color.getMaterialColor().col)
						.defaultLang().register();
			}
			ENDER_BACKPACK = REGISTRATE.item("ender_backpack", EnderBackpackItem::new).model(LightlandItems::createEnderBackpackModel).defaultLang().register();
			ENDER_POCKET = simpleItem("ender_pocket");
		}
	}

	private static void createBackpackModel(DataGenContext<Item, BackpackItem> ctx, RegistrateItemModelProvider pvd) {
		ItemModelBuilder builder = pvd.withExistingParent(ctx.getName(), "lightland:backpack");
		builder.override().predicate(new ResourceLocation("open"), 1).model(
				new ModelFile.UncheckedModelFile(LightLand.MODID + ":item/backpack_open"));
	}

	private static void createWorldChestModel(DataGenContext<Item, WorldChestItem> ctx, RegistrateItemModelProvider pvd) {
		ItemModelBuilder builder = pvd.withExistingParent(ctx.getName(), "lightland:dimensional_storage");
	}

	private static void createEnderBackpackModel(DataGenContext<Item, EnderBackpackItem> ctx, RegistrateItemModelProvider pvd) {
		pvd.withExistingParent("ender_backpack_open", "generated")
				.texture("layer0", "item/ender_backpack_open");
		ItemModelBuilder builder = pvd.withExistingParent("ender_backpack", "generated");
		builder.texture("layer0", "item/ender_backpack");
		builder.override().predicate(new ResourceLocation("open"), 1).model(
				new ModelFile.UncheckedModelFile(LightLand.MODID + ":item/ender_backpack_open"));
	}

	public static void register() {
	}

	public static ItemEntry<Item> simpleItem(String id) {
		return REGISTRATE.item(id, Item::new).defaultModel().defaultLang().register();
	}

}
