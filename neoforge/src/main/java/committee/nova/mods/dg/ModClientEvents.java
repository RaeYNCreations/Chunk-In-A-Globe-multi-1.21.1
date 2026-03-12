package committee.nova.mods.dg;

import committee.nova.mods.dg.common.item.GlobeBlockItem;
import committee.nova.mods.dg.common.net.payload.UpdateRequestPayload;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static committee.nova.mods.dg.utils.GlobeSectionManagerClient.*;

/**
 * Client-only event handlers for NeoForge 1.21.1.
 *
 * Must be a separate top-level class from ModEvents because NeoForge does not
 * support nested @EventBusSubscriber classes — only top-level classes are scanned.
 *
 * Replaces the inner ClientEvents class that was incorrectly nested in ModEvents,
 * and replaces the Forge TickEvent.ClientTickEvent handler.
 *
 * ClientTickEvent.Post replaces TickEvent.ClientTickEvent (no Phase check needed —
 * Post is equivalent to the old Phase.END; use .Pre for Phase.START).
 */
@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().level == null) {
            updateQueue.clear();
            selectionMap.clear();
            innerSelectionMap.clear();
            return;
        }

        var player = Minecraft.getInstance().player;
        if (player == null) return;

        if (Minecraft.getInstance().level.getGameTime() % 20 == 0
                || player.getMainHandItem().getItem() instanceof GlobeBlockItem) {

            UpdateRequestPayload payload = new UpdateRequestPayload(
                    updateQueue.size(), new IntOpenHashSet(updateQueue));
            PacketDistributor.sendToServer(payload);
            updateQueue.clear();
        }
    }
}
