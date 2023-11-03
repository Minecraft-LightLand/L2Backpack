package dev.xkmc.l2backpack.init.registrate;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2backpack.content.backpack.BackpackItem;
import dev.xkmc.l2backpack.content.backpack.EnderBackpackItem;
import dev.xkmc.l2backpack.content.bag.BookBag;
import dev.xkmc.l2backpack.content.bag.EquipmentBag;
import dev.xkmc.l2backpack.content.drawer.DrawerItem;
import dev.xkmc.l2backpack.content.quickswap.armorswap.ArmorSwap;
import dev.xkmc.l2backpack.content.quickswap.merged.EnderSwitch;
import dev.xkmc.l2backpack.content.quickswap.merged.MultiSwitch;
import dev.xkmc.l2backpack.content.quickswap.quiver.Quiver;
import dev.xkmc.l2backpack.content.quickswap.scabbard.Scabbard;
import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerItem;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestItem;
import dev.xkmc.l2backpack.content.tool.PickupTweakerTool;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.TagGen;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.Objects;

import static dev.xkmc.l2backpack.init.L2Backpack.REGISTRATE;

@SuppressWarnings({"unchecked", "unsafe"})
@MethodsReturnNonnullByDefault
public class BackpackItems {

	public static final RegistryEntry<CreativeModeTab> TAB = REGISTRATE
			.buildL2CreativeTab("backpack", "L2 Backpack",
					e -> e.icon(BackpackItems.ENDER_BACKPACK::asStack));

	// -------- common --------
	public static final ItemEntry<BackpackItem>[] BACKPACKS;
	public static final ItemEntry<WorldChestItem>[] DIMENSIONAL_STORAGE;
	public static final ItemEntry<EnderBackpackItem> ENDER_BACKPACK;
	public static final ItemEntry<Item> ENDER_POCKET;
	public static final ItemEntry<PickupTweakerTool> PICKUP_TWEAKER;

	public static final ItemEntry<EquipmentBag> ARMOR_BAG;
	public static final ItemEntry<BookBag> BOOK_BAG;
	public static final ItemEntry<Quiver> QUIVER;
	public static final ItemEntry<Scabbard> SCABBARD;
	public static final ItemEntry<ArmorSwap> ARMOR_SWAP;
	public static final ItemEntry<MultiSwitch> MULTI_SWITCH;
	public static final ItemEntry<EnderSwitch> ENDER_SWITCH;

	public static final ItemEntry<DrawerItem> DRAWER;
	public static final ItemEntry<EnderDrawerItem> ENDER_DRAWER;


	static {
		ITagManager<Item> manager = Objects.requireNonNull(ForgeRegistries.ITEMS.tags());
		TagKey<Item> curios_tag = manager.createTagKey(new ResourceLocation("curios", "back"));
		// Backpacks
		{
			BACKPACKS = new ItemEntry[16];
			for (int i = 0; i < 16; i++) {
				DyeColor color = DyeColor.values()[i];
				BACKPACKS[i] = REGISTRATE.item("backpack_" + color.getName(), p -> new BackpackItem(color, p))
						.tag(TagGen.BACKPACKS, curios_tag)
						.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(
								new ModelFile.UncheckedModelFile("builtin/entity")))
						.lang(RegistrateLangProvider.toEnglishName(color.getName() + "_backpack"))
						.register();
			}
			DIMENSIONAL_STORAGE = new ItemEntry[16];
			for (int i = 0; i < 16; i++) {
				DyeColor color = DyeColor.values()[i];
				DIMENSIONAL_STORAGE[i] = REGISTRATE.item("dimensional_storage_" + color.getName(), p -> new WorldChestItem(color, p))
						.tag(TagGen.DIMENSIONAL_STORAGES, curios_tag)
						.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(
								new ModelFile.UncheckedModelFile("builtin/entity")))
						.lang(RegistrateLangProvider.toEnglishName(color.getName() + "_dimensional_backpack")).register();
			}
			ENDER_BACKPACK = REGISTRATE.item("ender_backpack", EnderBackpackItem::new).model(BackpackItems::createEnderBackpackModel)
					.tag(curios_tag, TagGen.ENDER_CHEST).defaultLang().register();

			ENDER_POCKET = simpleItem("ender_pocket");

			PICKUP_TWEAKER = REGISTRATE.item("pickup_tweaker_tool", p -> new PickupTweakerTool(p.stacksTo(1)))
					.defaultModel().defaultLang().register();

			ARMOR_BAG = REGISTRATE.item("armor_bag", EquipmentBag::new).tag(TagGen.BAGS).defaultModel().lang("Equipment Bag").register();
			BOOK_BAG = REGISTRATE.item("book_bag", BookBag::new).tag(TagGen.BAGS).defaultModel().defaultLang().register();
			QUIVER = REGISTRATE.item("arrow_bag", Quiver::new).model(BackpackItems::createArrowBagModel)
					.tag(curios_tag, TagGen.SWAPS).lang("Quiver").register();
			SCABBARD = REGISTRATE.item("tool_swap", Scabbard::new).defaultModel().tag(curios_tag, TagGen.SWAPS).defaultLang().register();
			ARMOR_SWAP = REGISTRATE.item("armor_swap", ArmorSwap::new).defaultModel().tag(curios_tag, TagGen.SWAPS).defaultLang().register();
			MULTI_SWITCH = REGISTRATE.item("combined_swap", MultiSwitch::new).defaultModel().tag(curios_tag, TagGen.SWAPS).defaultLang().register();
			ENDER_SWITCH = REGISTRATE.item("ender_swap", EnderSwitch::new).defaultModel().tag(curios_tag, TagGen.SWAPS, TagGen.ENDER_CHEST).defaultLang().register();

			DRAWER = REGISTRATE.item("drawer", p -> new DrawerItem(BackpackBlocks.DRAWER.get(), p))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(
							new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(TagGen.DRAWERS).defaultLang().register();

			ENDER_DRAWER = REGISTRATE.item("ender_drawer", p -> new EnderDrawerItem(BackpackBlocks.ENDER_DRAWER.get(), p))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(
							new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(TagGen.DRAWERS).defaultLang().register();
		}
	}

	private static void createBackpackModel(DataGenContext<Item, BackpackItem> ctx, RegistrateItemModelProvider pvd) {
		ItemModelBuilder builder = pvd.withExistingParent(ctx.getName(), "l2backpack:backpack");
		builder.override().predicate(new ResourceLocation("open"), 1).model(
				new ModelFile.UncheckedModelFile(L2Backpack.MODID + ":item/backpack_open"));
	}

	private static void createWorldChestModel(DataGenContext<Item, WorldChestItem> ctx, RegistrateItemModelProvider pvd) {
		pvd.withExistingParent(ctx.getName(), "l2backpack:dimensional_storage");
	}

	private static void createEnderBackpackModel(DataGenContext<Item, EnderBackpackItem> ctx, RegistrateItemModelProvider pvd) {
		pvd.withExistingParent("ender_backpack_open", "generated")
				.texture("layer0", "item/ender_backpack_open");
		ItemModelBuilder builder = pvd.withExistingParent("ender_backpack", "generated");
		builder.texture("layer0", "item/ender_backpack");
		builder.override().predicate(new ResourceLocation("open"), 1).model(
				new ModelFile.UncheckedModelFile(L2Backpack.MODID + ":item/ender_backpack_open"));
	}


	public static <T extends Quiver> void createArrowBagModel(DataGenContext<Item, T> ctx, RegistrateItemModelProvider pvd) {
		ItemModelBuilder builder = pvd.withExistingParent(ctx.getName(), "generated");
		builder.texture("layer0", "item/" + ctx.getName() + "_0");
		for (int i = 1; i < 4; i++) {
			String name = ctx.getName() + "_" + i;
			ItemModelBuilder ret = pvd.withExistingParent(name, "generated");
			ret.texture("layer0", "item/" + name);
			ItemModelBuilder.OverrideBuilder override = builder.override();
			override.predicate(new ResourceLocation(L2Backpack.MODID, "arrow"), i);
			override.model(new ModelFile.UncheckedModelFile(L2Backpack.MODID + ":item/" + name));
		}
	}

	public static void register() {
	}

	public static ItemEntry<Item> simpleItem(String id) {
		return REGISTRATE.item(id, Item::new).defaultModel().defaultLang().register();
	}

}
