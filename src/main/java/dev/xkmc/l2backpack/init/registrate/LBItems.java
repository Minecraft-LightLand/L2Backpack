package dev.xkmc.l2backpack.init.registrate;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2backpack.content.backpack.BackpackItem;
import dev.xkmc.l2backpack.content.bag.BookBag;
import dev.xkmc.l2backpack.content.bag.EquipmentBag;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.client.LBBEWLR;
import dev.xkmc.l2backpack.content.drawer.DrawerItem;
import dev.xkmc.l2backpack.content.quickswap.handswap.HandswapItem;
import dev.xkmc.l2backpack.content.quickswap.handswap.MatcherData;
import dev.xkmc.l2backpack.content.quickswap.set.ArmorSetSwap;
import dev.xkmc.l2backpack.content.quickswap.single.ArmorSwap;
import dev.xkmc.l2backpack.content.quickswap.single.Quiver;
import dev.xkmc.l2backpack.content.quickswap.single.Scabbard;
import dev.xkmc.l2backpack.content.remote.dimensional.DimensionalItem;
import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerItem;
import dev.xkmc.l2backpack.content.remote.player.EnderBackpackItem;
import dev.xkmc.l2backpack.content.tool.DestroyTweakerTool;
import dev.xkmc.l2backpack.content.tool.PickupTweakerTool;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.LBTagGen;
import dev.xkmc.l2core.init.reg.registrate.SimpleEntry;
import dev.xkmc.l2core.init.reg.simple.DCReg;
import dev.xkmc.l2core.init.reg.simple.DCVal;
import dev.xkmc.l2core.util.DCStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import java.util.UUID;

import static dev.xkmc.l2backpack.init.L2Backpack.REGISTRATE;

@SuppressWarnings({"unchecked", "unsafe"})
@MethodsReturnNonnullByDefault
public class LBItems {

	public static final SimpleEntry<CreativeModeTab> TAB = REGISTRATE
			.buildL2CreativeTab("backpack", "L2 Backpack",
					e -> e.icon(LBItems.ENDER_BACKPACK::asStack));

	// -------- common --------
	public static final ItemEntry<BackpackItem>[] BACKPACKS;
	public static final ItemEntry<DimensionalItem>[] DIMENSIONAL_STORAGE;
	public static final ItemEntry<EnderBackpackItem> ENDER_BACKPACK;
	public static final ItemEntry<Item> ENDER_POCKET;
	public static final ItemEntry<PickupTweakerTool> PICKUP_TWEAKER;
	public static final ItemEntry<DestroyTweakerTool> DESTROY_TWEAKER;

	public static final ItemEntry<EquipmentBag> ARMOR_BAG;
	public static final ItemEntry<BookBag> BOOK_BAG;
	public static final ItemEntry<Quiver> QUIVER;
	public static final ItemEntry<Scabbard> SCABBARD;
	public static final ItemEntry<ArmorSwap> ARMOR_SWAP;
	public static final ItemEntry<ArmorSetSwap> SUIT_SWAP;
	public static final ItemEntry<HandswapItem> HANDSWAP;

	public static final ItemEntry<DrawerItem> DRAWER;
	public static final ItemEntry<EnderDrawerItem> ENDER_DRAWER;

	// components
	private static final DCReg DC = DCReg.of(L2Backpack.REG);
	public static final DCVal<Integer> DC_ROW = DC.intVal("row");
	public static final DCVal<Integer> DC_SEL = DC.intVal("selected");
	public static final DCVal<UUID> DC_CONT_ID = DC.uuid("container_id");
	public static final DCVal<UUID> DC_OWNER_ID = DC.uuid("owner_id");
	public static final DCVal<Component> DC_OWNER_NAME = DC.component("owner_name");
	public static final DCVal<Long> DC_PASSWORD = DC.longVal("password");
	public static final DCVal<String> DC_LOOT_ID = DC.str("loot_table");
	public static final DCVal<Long> DC_LOOT_SEED = DC.longVal("loot_seed");
	public static final DCVal<PickupConfig> DC_PICKUP = DC.reg("pickup", PickupConfig.class, true);
	public static final DCVal<Item> DC_ENDER_DRAWER_ITEM = DC.reg("ender_drawer_item",
			BuiltInRegistries.ITEM.byNameCodec(), ByteBufCodecs.registry(Registries.ITEM), true);
	public static final DCVal<DCStack> DC_DRAWER_STACK = DC.stack("drawer_stack");
	public static final DCVal<Integer> DC_DRAWER_COUNT = DC.intVal("drawer_count");
	public static final DCVal<Integer> DC_DRAWER_STACKING = DC.intVal("drawer_upgrade");
	public static final DCVal<Long> DC_SWAP_TOGGLE = DC.longVal("set_swap_toggle");
	public static final DCVal<MatcherData> DC_MATCHER = DC.reg("matcher", MatcherData.class, true);

	public static final DCVal<ItemContainerContents> BAG_CONTENT = DC.reg("bag_content",
			ItemContainerContents.CODEC, ItemContainerContents.STREAM_CODEC, true);
	public static final DCVal<ItemContainerContents> BACKPACK_CONTENT = DC.reg("backpack_content",
			ItemContainerContents.CODEC, ItemContainerContents.STREAM_CODEC, true);

	static {
		TagKey<Item> back = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "back"));
		TagKey<Item> belt = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "belt"));
		// Backpacks
		{
			BACKPACKS = new ItemEntry[16];
			for (int i = 0; i < 16; i++) {
				DyeColor color = DyeColor.values()[i];
				BACKPACKS[i] = REGISTRATE.item("backpack_" + color.getName(), p -> new BackpackItem(color, p))
						.tag(LBTagGen.BACKPACKS, back)
						.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(
								new ModelFile.UncheckedModelFile("builtin/entity")))
						.lang(RegistrateLangProvider.toEnglishName(color.getName() + "_backpack"))
						.clientExtension(() -> () -> LBBEWLR.EXTENSIONS)
						.register();
			}
			DIMENSIONAL_STORAGE = new ItemEntry[16];
			for (int i = 0; i < 16; i++) {
				DyeColor color = DyeColor.values()[i];
				DIMENSIONAL_STORAGE[i] = REGISTRATE.item("dimensional_storage_" + color.getName(), p -> new DimensionalItem(color, p))
						.tag(LBTagGen.DIMENSIONAL_STORAGES, back)
						.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(
								new ModelFile.UncheckedModelFile("builtin/entity")))
						.lang(RegistrateLangProvider.toEnglishName(color.getName() + "_dimensional_backpack"))
						.clientExtension(() -> () -> LBBEWLR.EXTENSIONS)
						.register();
			}
			ENDER_BACKPACK = REGISTRATE.item("ender_backpack", EnderBackpackItem::new)
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(
							new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(back, LBTagGen.ENDER_CHEST).defaultLang()
					.clientExtension(() -> () -> LBBEWLR.EXTENSIONS)
					.register();

			ENDER_POCKET = simpleItem("ender_pocket");

			PICKUP_TWEAKER = REGISTRATE.item("pickup_tweaker_tool", p -> new PickupTweakerTool(p.stacksTo(1)))
					.defaultModel().defaultLang().register();
			DESTROY_TWEAKER = REGISTRATE.item("destroy_tweaker_tool", p -> new DestroyTweakerTool(p.stacksTo(1)))
					.defaultModel().defaultLang().register();

			ARMOR_BAG = REGISTRATE.item("armor_bag", EquipmentBag::new).tag(LBTagGen.BAGS)
					.model((ctx, pvd) -> pvd.generated(ctx).override()
							.predicate(L2Backpack.loc("fill"), 1)
							.model(pvd.getBuilder(ctx.getName() + "_filled")
									.parent(new ModelFile.UncheckedModelFile("item/generated"))
									.texture("layer0", pvd.modLoc("item/" + ctx.getName() + "_filled"))))
					.lang("Equipment Bag").register();
			BOOK_BAG = REGISTRATE.item("book_bag", BookBag::new).tag(LBTagGen.BAGS)
					.model((ctx, pvd) -> pvd.generated(ctx).override()
							.predicate(L2Backpack.loc("fill"), 1)
							.model(pvd.getBuilder(ctx.getName() + "_filled")
									.parent(new ModelFile.UncheckedModelFile("item/generated"))
									.texture("layer0", pvd.modLoc("item/" + ctx.getName() + "_filled"))))
					.defaultLang().register();
			QUIVER = REGISTRATE.item("arrow_bag", Quiver::new).model(LBItems::createArrowBagModel)
					.tag(back, LBTagGen.SWAPS).lang("Quiver").register();
			SCABBARD = REGISTRATE.item("tool_swap", Scabbard::new).defaultModel().tag(back, LBTagGen.SWAPS).defaultLang().register();
			ARMOR_SWAP = REGISTRATE.item("armor_swap", ArmorSwap::new).defaultModel().tag(back, LBTagGen.SWAPS).defaultLang().register();
			SUIT_SWAP = REGISTRATE.item("suit_swap", ArmorSetSwap::new).defaultModel().tag(back, LBTagGen.SWAPS).defaultLang().register();
			HANDSWAP = REGISTRATE.item("hand_swap", HandswapItem::new).defaultModel().tag(belt).defaultLang().register();

			DRAWER = REGISTRATE.item("drawer", p -> new DrawerItem(LBBlocks.DRAWER.get(), p))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(
							new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(LBTagGen.DRAWERS).defaultLang()
					.clientExtension(() -> () -> LBBEWLR.EXTENSIONS)
					.register();

			ENDER_DRAWER = REGISTRATE.item("ender_drawer", p -> new EnderDrawerItem(LBBlocks.ENDER_DRAWER.get(), p))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(
							new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(LBTagGen.DRAWERS).defaultLang()
					.clientExtension(() -> () -> LBBEWLR.EXTENSIONS)
					.register();
		}
	}

	public static <T extends Quiver> void createArrowBagModel(DataGenContext<Item, T> ctx, RegistrateItemModelProvider pvd) {
		ItemModelBuilder builder = pvd.withExistingParent(ctx.getName(), "generated");
		builder.texture("layer0", "item/" + ctx.getName() + "_0");
		for (int i = 1; i < 4; i++) {
			String name = ctx.getName() + "_" + i;
			ItemModelBuilder ret = pvd.withExistingParent(name, "generated");
			ret.texture("layer0", "item/" + name);
			ItemModelBuilder.OverrideBuilder override = builder.override();
			override.predicate(L2Backpack.loc("arrow"), i);
			override.model(new ModelFile.UncheckedModelFile(L2Backpack.MODID + ":item/" + name));
		}
	}

	public static void register() {
	}

	public static ItemEntry<Item> simpleItem(String id) {
		return REGISTRATE.item(id, Item::new).defaultModel().defaultLang().register();
	}

}
