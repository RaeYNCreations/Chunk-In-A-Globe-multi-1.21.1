package committee.nova.mods.dg.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;

// 1.21.1: PortalInfo removed. Entity#changeDimension(DimensionTransition) is the new API.
public class NeoForgeDimensionHelper extends DimensionHelper {

    @Override
    public void changeDimension(Entity teleported, ServerLevel dimension, DimensionTransition transition) {
        teleported.changeDimension(transition);
    }
}
