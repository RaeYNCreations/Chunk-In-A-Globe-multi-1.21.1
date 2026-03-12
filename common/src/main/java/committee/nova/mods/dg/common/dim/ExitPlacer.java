package committee.nova.mods.dg.common.dim;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

public class ExitPlacer {

    private BlockPos blockPos;

    public ExitPlacer(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public DimensionTransition placeEntity(Entity teleported, ServerLevel destination) {
        if (blockPos == null && teleported instanceof ServerPlayer sp) {
            // Try to use the player's bed/respawn position if we don't have a stored return pos.
            // findRespawnPositionAndUseSpawnBlock signature changed in 1.21, so we just use
            // the stored sleeping pos directly as a best-effort location.
            blockPos = sp.getSleepingPos().orElse(null);
        }
        if (blockPos == null) {
            blockPos = destination.getSharedSpawnPos();
        }
        return new DimensionTransition(destination, Vec3.atBottomCenterOf(blockPos),
                Vec3.ZERO, 0f, 0f, DimensionTransition.DO_NOTHING);
    }
}
