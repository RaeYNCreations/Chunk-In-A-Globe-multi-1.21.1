package committee.nova.mods.dg;

import committee.nova.mods.dg.events.BaseEventHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.Objects;

/**
 * Server-side game-event handlers for NeoForge 1.21.1.
 *
 * Key differences from Forge 1.20.1:
 *  - @Mod.EventBusSubscriber → @EventBusSubscriber; modid is now required.
 *  - Client-side tick is in a separate class (ModClientEvents) annotated with
 *    Dist.CLIENT.  Nested @EventBusSubscriber inner classes are not supported —
 *    NeoForge only scans top-level classes for the annotation.
 *  - TickEvent.LevelTickEvent → LevelTickEvent.Pre / .Post (neoforge.event.tick).
 *    Subscribe to the Pre variant to match the old Phase.START check.
 */
@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ModEvents {

    /**
     * LevelTickEvent.Pre fires at the start of the level tick.
     * Replaces: TickEvent.LevelTickEvent with event.phase == Phase.START.
     */
    @SubscribeEvent
    public static void onLevelTickPre(LevelTickEvent.Pre event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            BaseEventHandler.onWorldLTickPre(serverLevel);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (Objects.requireNonNull(
                BaseEventHandler.onLeftClickBlock(event.getLevel(), event.getPos()))
                == InteractionResult.FAIL) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (Objects.requireNonNull(
                BaseEventHandler.onRightClickBlock(event.getLevel(), event.getEntity(), event.getHand()))
                == InteractionResult.FAIL) {
            event.setCanceled(true);
        }
    }
}
