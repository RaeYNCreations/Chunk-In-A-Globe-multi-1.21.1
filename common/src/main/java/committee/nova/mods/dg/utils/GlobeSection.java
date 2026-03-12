package committee.nova.mods.dg.utils;

import com.mojang.authlib.GameProfile;
import committee.nova.mods.dg.common.block.GlobeBlock;
import committee.nova.mods.dg.common.tile.GlobeBlockEntity;
import committee.nova.mods.dg.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobeSection {

	public static final int GLOBE_SIZE = 17;

	private final Map<BlockPos, BlockState> stateMap = new HashMap<>();
	private final Map<BlockPos, Integer> globeData = new HashMap<>();
	private final List<Entity> entities = new ArrayList<>();
	private final Map<Entity, Vec3> entityVec3dMap = new HashMap<>();

	public Map<BlockPos, BlockState> getStateMap() { return stateMap; }

	public void buildBlockMap(Level world, BlockPos origin) {
		stateMap.clear();
		final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		for (int x = 1; x < GLOBE_SIZE - 1; x++) {
			for (int y = 1; y < GLOBE_SIZE - 1; y++) {
				for (int z = 1; z < GLOBE_SIZE - 1; z++) {
					mutable.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
					BlockState state = world.getBlockState(mutable);
					if (!state.isAir()) {
						stateMap.put(new BlockPos(x, y, z), state);
					}
					if (state.getBlock() instanceof GlobeBlock) {
						GlobeBlockEntity gbe = (GlobeBlockEntity) world.getBlockEntity(mutable);
						if (gbe.getGlobeID() != -1) {
							globeData.put(new BlockPos(x, y, z), gbe.getGlobeID());
						}
					}
				}
			}
		}
	}

	public void buildEntityList(Level world, BlockPos origin) {
		entities.clear();
		entityVec3dMap.clear();
		entities.addAll(world.getEntities(null, new AABB(
				origin.getX(), origin.getY(), origin.getZ(),
				origin.getX() + GLOBE_SIZE, origin.getY() + GLOBE_SIZE, origin.getZ() + GLOBE_SIZE)));
	}

	public void fromBlockTag(CompoundTag tag) {
		stateMap.clear();
		globeData.clear();
		for (String key : tag.getAllKeys()) {
			CompoundTag entryTag = tag.getCompound(key);
			BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), entryTag.getCompound("state"));
			// 1.21.1: NbtUtils.readBlockPos(CompoundTag, String) — reads a named child.
			BlockPos pos = NbtUtils.readBlockPos(entryTag, "pos").orElse(BlockPos.ZERO);
			stateMap.put(pos, state);
			if (entryTag.contains("globe_data")) {
				globeData.put(pos, entryTag.getInt("globe_data"));
			}
		}
	}

	public CompoundTag toBlockTag() {
		CompoundTag compoundTag = new CompoundTag();
		for (Map.Entry<BlockPos, BlockState> entry : stateMap.entrySet()) {
			CompoundTag entryTag = new CompoundTag();
			entryTag.put("state", NbtUtils.writeBlockState(entry.getValue()));
			// 1.21.1: NbtUtils.writeBlockPos returns Tag — put it as a named child.
			entryTag.put("pos", NbtUtils.writeBlockPos(entry.getKey()));
			if (globeData.containsKey(entry.getKey())) {
				entryTag.putInt("globe_data", globeData.get(entry.getKey()));
			}
			compoundTag.put("entry_" + entry.getKey().toString(), entryTag);
		}
		return compoundTag;
	}

	public void fromEntityTag(CompoundTag tag, Level world) {
		entities.clear();
		entityVec3dMap.clear();
		for (String uuid : tag.getAllKeys()) {
			CompoundTag entityData = tag.getCompound(uuid);
			ResourceLocation entityType = ResourceLocation.parse(entityData.getString("entity_type"));

			if (entityType.toString().equals("minecraft:player")) {
				// 1.21.1: Minecraft.getInstance().getUser() returns User which no longer has getGameProfile().
				// Use User.getProfileId() + User.getName() to reconstruct a GameProfile.
				GameProfile gameProfile;
				if (entityData.contains("game_profile")) {
					// NbtUtils.readGameProfile was removed — reconstruct manually.
					CompoundTag gpTag = entityData.getCompound("game_profile");
					gameProfile = new GameProfile(
							gpTag.hasUUID("Id") ? gpTag.getUUID("Id") : java.util.UUID.randomUUID(),
							gpTag.getString("Name")
					);
				} else {
					var user = Minecraft.getInstance().getUser();
					gameProfile = new GameProfile(user.getProfileId(), user.getName());
				}
				RemotePlayer entity = new RemotePlayer((ClientLevel) world, gameProfile);
				entity.load(entityData.getCompound("entity_data"));
				entities.add(entity);
				entityVec3dMap.put(entity, new Vec3(
						entityData.getDouble("entity_x"),
						entityData.getDouble("entity_y"),
						entityData.getDouble("entity_z")));
				continue;
			}

			EntityType<?> type = Services.PLATFORM.getEntityTypeByKey(entityType);
			if (type != null) {
				Entity entity = type.create(world);
				if (entity == null) {
					System.out.println("Failed to create " + entityType);
					continue;
				}
				entity.load(entityData.getCompound("entity_data"));
				entity.setPos(0, 0, 0);
				entities.add(entity);
				entityVec3dMap.put(entity, new Vec3(
						entityData.getDouble("entity_x"),
						entityData.getDouble("entity_y"),
						entityData.getDouble("entity_z")));
			}
		}
	}

	public CompoundTag toEntityTag(BlockPos origin) {
		CompoundTag compoundTag = new CompoundTag();
		for (Entity entity : entities) {
			CompoundTag entityTag = new CompoundTag();
			ResourceLocation entityType = Services.PLATFORM.getKeyByEntityType(entity.getType());
			entityTag.putString("entity_type", entityType.toString());
			CompoundTag entityData = new CompoundTag();
			entity.save(entityData);
			entityData.remove("Passengers");
			entityTag.put("entity_data", entityData);

			Vec3 relativePos = entity.position().subtract(Vec3.atLowerCornerOf(origin));
			entityTag.putDouble("entity_x", relativePos.x());
			entityTag.putDouble("entity_y", relativePos.y());
			entityTag.putDouble("entity_z", relativePos.z());

			compoundTag.put(entity.getStringUUID(), entityTag);

			if (entityType.toString().equals("minecraft:player")) {
				Player playerEntity = (Player) entity;
				// NbtUtils.writeGameProfile was removed in 1.21.1 — write manually.
				CompoundTag gpTag = new CompoundTag();
				GameProfile gp = playerEntity.getGameProfile();
				gpTag.putUUID("Id", gp.getId());
				gpTag.putString("Name", gp.getName());
				compoundTag.put("game_profile", gpTag);
			}
		}
		return compoundTag;
	}

	public List<Entity> getEntities() { return entities; }
	public Map<Entity, Vec3> getEntityVec3dMap() { return entityVec3dMap; }
	public Map<BlockPos, Integer> getGlobeData() { return globeData; }
}
