package committee.nova.mods.dg;

import committee.nova.mods.dg.common.net.payload.SectionUpdatePayload;
import committee.nova.mods.dg.common.net.payload.UpdateRequestPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Registers the two custom packet payloads for NeoForge 1.21.1.
 *
 * Key difference from Forge 1.20.1 SimpleChannel:
 *  - No SimpleChannel / NetworkRegistry.newSimpleChannel().
 *  - Payloads implement CustomPacketPayload and carry their own TYPE + STREAM_CODEC.
 *  - Registration happens via RegisterPayloadHandlersEvent on the mod bus.
 *  - Direction is expressed by registering on playToServer vs playToClient.
 */
public class NeoNetworkInit {

    public static void register(IEventBus modBus) {
        modBus.addListener(NeoNetworkInit::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0");

        // Client → Server: client requests a globe section update
        registrar.playToServer(
                UpdateRequestPayload.TYPE,
                UpdateRequestPayload.STREAM_CODEC,
                UpdateRequestPayload::handle
        );

        // Server → Client: server pushes a section update to clients
        registrar.playToClient(
                SectionUpdatePayload.TYPE,
                SectionUpdatePayload.STREAM_CODEC,
                SectionUpdatePayload::handle
        );
    }
}
