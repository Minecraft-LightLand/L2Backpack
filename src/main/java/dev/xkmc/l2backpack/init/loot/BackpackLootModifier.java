package dev.xkmc.l2backpack.init.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xkmc.l2backpack.content.backpack.BackpackItem;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestItem;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import dev.xkmc.l2library.util.math.MathHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
					Codec.STRING.fieldOf("loot").forGetter(e -> e.loot.toString())))
			.apply(i, BackpackLootModifier::new));

	private final double chance;
	private final DyeColor color;
	private final String name;
	private final ResourceLocation loot;

	private BackpackLootModifier(LootItemCondition[] conditionsIn, double chance, int color, String name, String loot) {
		super(conditionsIn);
		this.chance = chance;
		this.color = DyeColor.values()[color % DyeColor.values().length];
		this.name = name;
		this.loot = new ResourceLocation(loot);
	}

	public BackpackLootModifier(double chance, LootGen.LootDefinition def, LootItemCondition... conditionsIn) {
		super(conditionsIn);
		this.chance = chance;
		this.color = def.color;
		this.name = def.player.id;
		this.loot = new ResourceLocation(L2Backpack.MODID, def.id);
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> list, LootContext context) {
		if (chance > context.getRandom().nextDouble()) {
			ItemStack stack;
			if (name.length() > 0) {
				stack = BackpackItems.DIMENSIONAL_STORAGE[color.ordinal()].asStack();
				WorldChestItem.initLootGen(stack, MathHelper.getUUIDFromString(name), L2Backpack.MODID + ".loot." + name + ".name", color, loot);
			} else {
				stack = BackpackItems.BACKPACKS[color.ordinal()].asStack();
				BackpackItem.initLootGen(stack, loot);
			}
			stack.setHoverName(Component.translatable(L2Backpack.MODID + ".loot." + name + ".item").withStyle(ChatFormatting.GOLD));
			list.add(stack);
		}
		return list;
	}

	@Override
	public Codec<BackpackLootModifier> codec() {
		return CODEC;
	}

}
