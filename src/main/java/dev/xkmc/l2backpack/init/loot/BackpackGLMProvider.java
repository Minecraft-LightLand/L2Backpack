package dev.xkmc.l2backpack.init.loot;

import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class BackpackGLMProvider extends GlobalLootModifierProvider {

	public BackpackGLMProvider(PackOutput gen) {
		super(gen, L2Backpack.MODID);
	}

	@Override
	protected void start() {
		for (LootGen.LootDefinition def : LootGen.LootDefinition.values()) {
			this.add(def.id, new BackpackLootModifier(def.chance, def, LootTableIdCondition.builder(def.target).build()));
		}
	}
}
