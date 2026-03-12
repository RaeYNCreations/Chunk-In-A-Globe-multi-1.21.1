package committee.nova.mods.dg.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;

// 1.21.1: PortalInfo was replaced by DimensionTransition.
// Entity#changeDimension(DimensionTransition) is called directly.
public abstract class DimensionHelper {
    public void changeDimension(Entity teleported, ServerLevel dimension, DimensionTransition transition) {
        teleported.changeDimension(transition);
    }
}
