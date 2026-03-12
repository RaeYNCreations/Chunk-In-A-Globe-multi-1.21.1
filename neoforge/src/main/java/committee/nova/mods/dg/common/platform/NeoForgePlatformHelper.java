package committee.nova.mods.dg.platform;

import committee.nova.mods.dg.platform.services.IPlatformHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

/**
 * NeoForge 1.21.1 platform helper.
 *
 * Key difference from Forge 1.20.1:
 *  - ForgeRegistries.BLOCKS / ENTITY_TYPES no longer exist in NeoForge 1.21.1.
 *    All vanilla registry lookups go through BuiltInRegistries directly.
 *  - ModList and FMLLoader imports shift from net.minecraftforge to net.neoforged.
 */
public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Block getBlockByKey(ResourceLocation key) {
        return BuiltInRegistries.BLOCK.get(key);
    }

    @Override
    public ResourceLocation getKeyByBlock(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    @Override
    public EntityType<?> getEntityTypeByKey(ResourceLocation key) {
        return BuiltInRegistries.ENTITY_TYPE.get(key);
    }

    @Override
    public ResourceLocation getKeyByEntityType(EntityType<?> entityType) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
    }
}
