package committee.nova.mods.dg.common.dim;

import committee.nova.mods.dg.CommonClass;
import committee.nova.mods.dg.common.tile.GlobeBlockEntity;
import committee.nova.mods.dg.utils.GlobeManager;
import committee.nova.mods.dg.utils.GlobeSection;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

// 1.21.1: PortalInfo → DimensionTransition.
public class GlobeDimensionPlacer {

    private int globeId = -1;
    private ResourceKey<Level> returnDimension = null;
    private BlockPos returnPos = null;
    private Block baseBlock = null;

    public GlobeDimensionPlacer() {}

    public GlobeDimensionPlacer(int globeId, ResourceKey<Level> dimensionType, BlockPos returnPos, Block baseBlock) {
        this.globeId = globeId;
        this.returnDimension = dimensionType;
        this.returnPos = returnPos;
        this.baseBlock = baseBlock;
    }

    public DimensionTransition placeEntity(Entity entity, ServerLevel serverWorld) {
        if (globeId == -1) throw new RuntimeException("Unknown globe: " + globeId);
        GlobeManager.Globe globe = GlobeManager.getInstance(serverWorld).getGlobeByID(globeId);

        BlockPos globePos = globe.getGlobeLocation();
        BlockPos spawnPos = globePos.offset(8, 1, 8);
        buildGlobe(serverWorld, globePos, spawnPos);

        return new DimensionTransition(serverWorld, Vec3.atBottomCenterOf(spawnPos),
                Vec3.ZERO, 0f, 0f, DimensionTransition.DO_NOTHING);
    }

    private void buildGlobe(ServerLevel world, BlockPos globePos, BlockPos spawnPos) {
        final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int x = 0; x < GlobeSection.GLOBE_SIZE; x++) {
            for (int y = 0; y < GlobeSection.GLOBE_SIZE; y++) {
                for (int z = 0; z < GlobeSection.GLOBE_SIZE; z++) {
                    if (x == 0 || x == GlobeSection.GLOBE_SIZE - 1
                            || y == 0 || y == GlobeSection.GLOBE_SIZE - 1
                            || z == 0 || z == GlobeSection.GLOBE_SIZE - 1) {
                        mutable.set(globePos.getX() + x, globePos.getY() + y, globePos.getZ() + z);
                        world.setBlockAndUpdate(mutable, Blocks.BARRIER.defaultBlockState());
                    }
                }
            }
        }
        world.setBlockAndUpdate(spawnPos.below(), CommonClass.globeBlock.defaultBlockState());
        GlobeBlockEntity exitBlockEntity = (GlobeBlockEntity) world.getBlockEntity(spawnPos.below());
        exitBlockEntity.setGlobeID(globeId);
        exitBlockEntity.setBaseBlock(baseBlock);
        if (returnPos != null && returnDimension != null) {
            exitBlockEntity.setReturnPos(returnPos, returnDimension);
        }
    }
}
