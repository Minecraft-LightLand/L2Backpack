package dev.xkmc.l2backpack.init.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestItem;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import dev.xkmc.l2library.util.math.MathHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class BackpackLootModifier extends LootModifier {

	public static final Codec<BackpackLootModifier> CODEC = RecordCodecBuilder.create(i -> codecStart(i).and(i.group(
					Codec.DOUBLE.fieldOf("chance").forGetter(e -> e.chance),
					Codec.INT.fieldOf("color").forGetter(e -> e.color.ordinal()),
					Codec.STRING.fieldOf("name").forGetter(e -> e.name),
					Codec.STRING.fieldOf("uuid_base").forGetter(e -> e.uuid_base),
					Codec.STRING.fieldOf("loot").forGetter(e -> e.loot.toString())))
			.apply(i, BackpackLootModifier::new));

	private final double chance;
	private final DyeColor color;
	private final String name;
	private final String uuid_base;
	private final ResourceLocation loot;

	private BackpackLootModifier(LootItemCondition[] conditionsIn, double chance, int color, String name, String uuid_base, String loot) {
		super(conditionsIn);
		this.chance = chance;
		this.color = DyeColor.values()[color % DyeColor.values().length];
		this.name = name;
		this.uuid_base = uuid_base;
		this.loot = new ResourceLocation(loot);
	}

	public BackpackLootModifier(double chance, LootGen.LootDefinition def, LootItemCondition... conditionsIn) {
		super(conditionsIn);
		this.chance = chance;
		this.color = def.color;
		this.name = def.player.id;
		this.uuid_base = def.player.id;
		this.loot = new ResourceLocation(L2Backpack.MODID, def.id);
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> list, LootContext context) {
		if (chance > context.getRandom().nextDouble()) {
			ItemStack stack = BackpackItems.DIMENSIONAL_STORAGE[color.ordinal()].asStack();
			list.add(WorldChestItem.initLootGen(stack, MathHelper.getUUIDFromString(uuid_base), name, color, loot));
		}
		return list;
	}

	@Override
	public Codec<BackpackLootModifier> codec() {
		return CODEC;
	}

}
