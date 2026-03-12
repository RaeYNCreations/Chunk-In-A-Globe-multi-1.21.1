package committee.nova.mods.dg.common.net.payload;

import committee.nova.mods.dg.Constants;
import committee.nova.mods.dg.utils.GlobeSection;
import committee.nova.mods.dg.utils.GlobeSectionManagerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Replaces SectionUpdatePkt + ForgeSectionUpdatePkt for NeoForge 1.21.1.
 *
 * The old Forge handler checked NetworkDirection.PLAY_TO_CLIENT to guard
 * client-only code.  In NeoForge 1.21.1 that is not needed: registering via
 * registrar.playToClient() already ensures the handler only runs on the client.
 * IPayloadContext.player() returns the local ClientPlayer on the client side.
 */
public record SectionUpdatePayload(int id, boolean inner, boolean blocks, CompoundTag tag)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SectionUpdatePayload> TYPE =
            new CustomPacketPayload.Type<>(Constants.rl("section_update"));

    /**
     * StreamCodec — mirrors the old SectionUpdatePkt(FriendlyByteBuf) and toBytes().
     * readAnySizeNbt() / writeNbt() are unchanged between Forge 1.20.1 and NeoForge 1.21.1.
     */
    public static final StreamCodec<FriendlyByteBuf, SectionUpdatePayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeInt(payload.id());
                        buf.writeBoolean(payload.inner());
                        buf.writeBoolean(payload.blocks());
                        buf.writeNbt(payload.tag());
                    },
                    buf -> new SectionUpdatePayload(
                            buf.readInt(),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            (net.minecraft.nbt.CompoundTag) buf.readNbt(net.minecraft.nbt.NbtAccounter.unlimitedHeap())
                    )
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * Client-side handler.
     * Replaces ForgeSectionUpdatePkt.handle(msg, Supplier<NetworkEvent.Context>).
     *
     * ctx.get().getSender().level() was used in the Forge version — on the client
     * side getSender() was null, so the original code had a latent bug there.
     * We use Minecraft.getInstance().level instead, which is the correct
     * client-side level.
     */
    public static void handle(SectionUpdatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level == null) return;

            final GlobeSection section = GlobeSectionManagerClient.getGlobeSection(payload.id(), payload.inner());
            if (payload.blocks()) {
                section.fromBlockTag(payload.tag());
            } else {
                section.fromEntityTag(payload.tag(), level);
            }
            GlobeSectionManagerClient.provideGlobeSectionUpdate(payload.inner(), payload.id(), section);
        });
    }
}
