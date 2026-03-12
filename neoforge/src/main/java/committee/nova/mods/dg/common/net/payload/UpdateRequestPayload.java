package committee.nova.mods.dg.common.net.payload;

import committee.nova.mods.dg.CommonClass;
import committee.nova.mods.dg.Constants;
import committee.nova.mods.dg.utils.GlobeManager;
import committee.nova.mods.dg.utils.GlobeSection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Replaces UpdateRequestPkt + ForgeUpdateRequestPkt for NeoForge 1.21.1.
 *
 * CustomPacketPayload differences from Forge 1.20.1 SimpleChannel:
 *  - The payload class itself declares TYPE (ResourceLocation wrapper) and
 *    STREAM_CODEC instead of separate encode/decode lambdas in registerMessage().
 *  - The handler receives (payload, IPayloadContext) instead of
 *    (msg, Supplier<NetworkEvent.Context>).
 *  - ctx.enqueueWork() is still available via IPayloadContext.enqueueWork().
 *  - ctx.getSender() is now context.sender() — returns Optional<ServerPlayer>
 *    on the server side, empty on the client.
 */
public record UpdateRequestPayload(int amount, IntSet updateQueue) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateRequestPayload> TYPE =
            new CustomPacketPayload.Type<>(Constants.rl("update_request"));

    /**
     * StreamCodec: encode/decode against FriendlyByteBuf.
     * Mirrors the old UpdateRequestPkt(FriendlyByteBuf) constructor and toBytes().
     */
    public static final StreamCodec<FriendlyByteBuf, UpdateRequestPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeInt(payload.amount());
                        for (int id : payload.updateQueue()) {
                            buf.writeInt(id);
                        }
                    },
                    buf -> {
                        int amount = buf.readInt();
                        IntSet queue = new IntOpenHashSet();
                        for (int i = 0; i < amount; i++) {
                            queue.add(buf.readInt());
                        }
                        return new UpdateRequestPayload(amount, queue);
                    }
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * Server-side handler.
     * Replaces ForgeUpdateRequestPkt.handle(msg, Supplier<NetworkEvent.Context>).
     *
     * IPayloadContext.enqueueWork() matches the old ctx.get().enqueueWork().
     * context.sender() replaces ctx.get().getSender() — it is non-null here
     * because this payload is registered playToServer.
     */
    public static void handle(UpdateRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            for (int id : payload.updateQueue()) {
                updateAndSyncToPlayer(player, id, true);
                updateAndSyncToPlayer(player, id, false);
            }
        });
    }

    private static void updateAndSyncToPlayer(ServerPlayer player, int globeID, boolean blocks) {
        if (globeID == -1) return;

        ServerLevel serverWorld = (ServerLevel) player.level();
        GlobeManager.Globe globe = GlobeManager.getInstance(serverWorld).getGlobeByID(globeID);
        ServerLevel globeWorld = serverWorld.getServer().getLevel(CommonClass.globeDimension);

        if (blocks) {
            globe.updateBlockSection(globeWorld, false, null);
        } else {
            globe.updateEntitySection(globeWorld, false, null);
        }

        GlobeSection section = globe.getGlobeSection(false);

        SectionUpdatePayload response;
        if (blocks) {
            response = new SectionUpdatePayload(globe.getId(), false, true, section.toBlockTag());
        } else {
            response = new SectionUpdatePayload(globe.getId(), false, false,
                    section.toEntityTag(globe.getGlobeLocation()));
        }

        PacketDistributor.sendToPlayer(player, response);
    }
}
