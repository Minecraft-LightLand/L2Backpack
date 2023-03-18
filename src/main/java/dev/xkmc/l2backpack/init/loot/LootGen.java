package dev.xkmc.l2backpack.init.loot;

import com.tterrag.registrate.providers.loot.RegistrateLootTableProvider;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2library.util.data.LootTableTemplate;
import dev.xkmc.l2library.util.math.MathHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Locale;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class LootGen {

	public enum HiddenPlayer {
		UNNAMED("unnamed", "the Unnamed Explorer", "Backpack of the Unnamed Explorer");

		public final String id;
		public final String pname, bname;
		public final UUID uuid;

		HiddenPlayer(String id, String def, String bname) {
			this.id = id;
			this.pname = def;
			this.bname = bname;
			this.uuid = MathHelper.getUUIDFromString(id);
		}

	}

	private static LootTable.Builder buildEndCityExtraLoot() {
		return LootTable.lootTable().withPool(LootTableTemplate.getPool(1, 0)
						.add(LootTableTemplate.getItem(Items.ELYTRA, 1)))
				.withPool(LootTableTemplate.getPool(2, 1)
						.add(LootTableTemplate.getItem(Items.ENCHANTED_GOLDEN_APPLE, 2, 4))
						.add(LootTableTemplate.getItem(Items.NETHERITE_INGOT, 2, 4))
						.add(LootTableTemplate.getItem(Items.NETHER_STAR, 1)))
				.withPool(LootTableTemplate.getPool(5, 2)
						.add(LootTableTemplate.getItem(Items.GLOWSTONE_DUST, 16, 32))
						.add(LootTableTemplate.getItem(Items.REDSTONE, 16, 32))
						.add(LootTableTemplate.getItem(Items.LAPIS_LAZULI, 16, 32))
						.add(LootTableTemplate.getItem(Items.AMETHYST_SHARD, 16, 32))
						.add(LootTableTemplate.getItem(Items.QUARTZ, 16, 32))
						.add(LootTableTemplate.getItem(Items.EMERALD, 16, 32)));
	}

	private static LootTable.Builder buildPlaceholderLoot() {
		return LootTable.lootTable().withPool(LootTableTemplate.getPool(1, 0)
				.add(LootTableTemplate.getItem(Items.COAL, 1, 16)));
	}

	public enum LootDefinition {
		END_CITY_TREASURE(1, HiddenPlayer.UNNAMED, DyeColor.MAGENTA, BuiltInLootTables.END_CITY_TREASURE, LootGen::buildEndCityExtraLoot),
		BASTION_TREASURE(1, HiddenPlayer.UNNAMED, DyeColor.BLACK, BuiltInLootTables.BASTION_TREASURE, LootGen::buildPlaceholderLoot),
		DESERT_PYRAMID(1, HiddenPlayer.UNNAMED, DyeColor.YELLOW, BuiltInLootTables.DESERT_PYRAMID, LootGen::buildPlaceholderLoot),
		ANCIENT_CITY(1, HiddenPlayer.UNNAMED, DyeColor.CYAN, BuiltInLootTables.ANCIENT_CITY, LootGen::buildPlaceholderLoot),
		SHIPWRECK_TREASURE(1, HiddenPlayer.UNNAMED, DyeColor.BLUE, BuiltInLootTables.SHIPWRECK_TREASURE, LootGen::buildPlaceholderLoot),
		UNDERWATER_RUIN_BIG(1, HiddenPlayer.UNNAMED, DyeColor.LIGHT_BLUE, BuiltInLootTables.UNDERWATER_RUIN_BIG, LootGen::buildPlaceholderLoot),
		VILLAGE_CARTOGRAPHER(1, HiddenPlayer.UNNAMED, DyeColor.WHITE, BuiltInLootTables.VILLAGE_CARTOGRAPHER, LootGen::buildPlaceholderLoot),
		IGLOO_CHEST(1, HiddenPlayer.UNNAMED, DyeColor.LIGHT_GRAY, BuiltInLootTables.IGLOO_CHEST, LootGen::buildPlaceholderLoot),
		STRONGHOLD_CORRIDOR(1, HiddenPlayer.UNNAMED, DyeColor.GRAY, BuiltInLootTables.STRONGHOLD_CORRIDOR, LootGen::buildPlaceholderLoot),
		WOODLAND_MANSION(1, HiddenPlayer.UNNAMED, DyeColor.BROWN, BuiltInLootTables.WOODLAND_MANSION, LootGen::buildPlaceholderLoot),
		NETHER_BRIDGE(1, HiddenPlayer.UNNAMED, DyeColor.RED, BuiltInLootTables.NETHER_BRIDGE, LootGen::buildPlaceholderLoot),
		PILLAGER_OUTPOST(1, HiddenPlayer.UNNAMED, DyeColor.ORANGE, BuiltInLootTables.PILLAGER_OUTPOST, LootGen::buildPlaceholderLoot),
		RUINED_PORTAL(1, HiddenPlayer.UNNAMED, DyeColor.MAGENTA, BuiltInLootTables.RUINED_PORTAL, LootGen::buildPlaceholderLoot),
		ABANDONED_MINESHAFT(1, HiddenPlayer.UNNAMED, DyeColor.PINK, BuiltInLootTables.ABANDONED_MINESHAFT, LootGen::buildPlaceholderLoot),
		JUNGLE_TEMPLE(1, HiddenPlayer.UNNAMED, DyeColor.GRAY, BuiltInLootTables.JUNGLE_TEMPLE, LootGen::buildPlaceholderLoot),
		SIMPLE_DUNGEON(1, HiddenPlayer.UNNAMED, DyeColor.LIME, BuiltInLootTables.SIMPLE_DUNGEON, LootGen::buildPlaceholderLoot),
		;

		public final String id;
		public final double chance;
		public final HiddenPlayer player;
		public final DyeColor color;
		public final ResourceLocation target;
		public final Supplier<LootTable.Builder> loot;


		LootDefinition(double chance, HiddenPlayer player, DyeColor color, ResourceLocation target, Supplier<LootTable.Builder> loot) {
			this.chance = chance;
			this.id = name().toLowerCase(Locale.ROOT);
			this.player = player;
			this.color = color;
			this.target = target;
			this.loot = loot;
		}
	}

	private static void genBagLoot(BiConsumer<ResourceLocation, LootTable.Builder> map) {
		for (LootDefinition def : LootDefinition.values()) {
			map.accept(new ResourceLocation(L2Backpack.MODID, def.id), def.loot.get());
		}
	}

	public static void genLoot(RegistrateLootTableProvider pvd) {
		pvd.addLootAction(LootContextParamSets.EMPTY, LootGen::genBagLoot);
	}

}
