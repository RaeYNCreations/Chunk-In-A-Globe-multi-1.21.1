package committee.nova.mods.dg.common.net;

import committee.nova.mods.dg.common.net.payload.SectionUpdatePayload;
import committee.nova.mods.dg.common.tile.GlobeBlockEntity;
import committee.nova.mods.dg.utils.GlobeManager;
import committee.nova.mods.dg.utils.GlobeSection;
import committee.nova.mods.dg.utils.GlobeSectionManagerServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/**
 * NeoForge 1.21.1 replacement for ForgeGlobeSectionManagerServer.
 *
 * PacketDistributor usage difference from Forge 1.20.1:
 *  - Old: CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), pkt)
 *  - New: PacketDistributor.sendToPlayer(player, payload)
 *    (static methods, no channel wrapper needed)
 */
public class NeoForgeGlobeSectionManagerServer extends GlobeSectionManagerServer {

    @Override
    public void syncToPlayers(GlobeBlockEntity blockEntity, GlobeSection section,
                              GlobeManager.Globe globe, List<ServerPlayer> nearbyPlayers,
                              boolean blocks) {

        SectionUpdatePayload payload;
        if (blocks) {
            payload = new SectionUpdatePayload(
                    globe.getId(), blockEntity.isInner(), true, section.toBlockTag());
        } else {
            payload = new SectionUpdatePayload(
                    globe.getId(), blockEntity.isInner(), false,
                    section.toEntityTag(blockEntity.isInner()
                            ? blockEntity.getInnerScanPos()
                            : globe.getGlobeLocation()));
        }

        for (ServerPlayer player : nearbyPlayers) {
            PacketDistributor.sendToPlayer(player, payload);
        }
    }
}
