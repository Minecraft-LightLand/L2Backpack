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
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class LootGen {

	public enum HiddenPlayer {
		UNNAMED("unnamed", "the unnamed explorer");

		public final String id;
		public final String def;
		public final UUID uuid;

		HiddenPlayer(String id, String def) {
			this.id = L2Backpack.MODID + ".names." + id;
			this.def = def;
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

	public enum LootDefinition {
		END_CITY("end_city", HiddenPlayer.UNNAMED, DyeColor.MAGENTA, BuiltInLootTables.END_CITY_TREASURE, LootGen::buildEndCityExtraLoot);

		public final String id;
		public final HiddenPlayer player;
		public final DyeColor color;
		public final ResourceLocation target;
		public final Supplier<LootTable.Builder> loot;


		LootDefinition(String id, HiddenPlayer player, DyeColor color, ResourceLocation target, Supplier<LootTable.Builder> loot) {
			this.id = id;
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
