package dev.xkmc.l2backpack.content.remote.common;

import dev.xkmc.l2core.capability.level.BaseSavedData;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@SerialClass
public class LBSavedData extends BaseSavedData<LBSavedData> {

	private static final String ID = "l2backpack_dimensional";
	private static final Factory<LBSavedData> FACTORY = new Factory<>(LBSavedData::new, LBSavedData::new);

	public static LBSavedData get(ServerLevel level) {
		var ans = level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, ID);
		ans.level = level;
		return ans;
	}

	@SerialField
	protected final HashMap<UUID, LBUserData> byPlayer = new HashMap<>();

	private ServerLevel level;

	private LBSavedData() {
		super(LBSavedData.class);
	}

	private LBSavedData(CompoundTag tag, HolderLookup.Provider pvd) {
		super(LBSavedData.class);
		new TagCodec(pvd).fromTag(tag, LBSavedData.class, this);
	}

	protected LBUserData get(UUID id) {
		return byPlayer.computeIfAbsent(id, l -> new LBUserData());
	}

	public Optional<StorageContainer> getOrCreateStorage(UUID id, int color, long password,
														 @Nullable ServerPlayer player,
														 @Nullable ResourceLocation loot,
														 long seed) {
		StorageContainer storage = get(id).getStorage(color, id);
		if (!storage.init) {
			storage.init = true;
			storage.password = password;
			if (loot != null) {
				LootTable loottable = level.getServer().reloadableRegistries()
						.getLootTable(ResourceKey.create(Registries.LOOT_TABLE, loot));
				LootParams.Builder builder = new LootParams.Builder(level);
				if (player != null) {
					builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
				}
				loottable.fill(storage.get(), builder.create(LootContextParamSets.EMPTY), seed);
			}
		}
		if (storage.password == password)
			return Optional.of(storage);
		return Optional.empty();
	}

	public Optional<StorageContainer> getStorageWithoutPassword(UUID id, int color) {
		StorageContainer ans = get(id).getStorage(color, id);
		if (!ans.init) return Optional.empty();
		return Optional.of(ans);
	}

	public EnderDrawerAccess getOrCreateDrawer(UUID id, Item item) {
		return new EnderDrawerAccess(this, id, item);
	}

}
