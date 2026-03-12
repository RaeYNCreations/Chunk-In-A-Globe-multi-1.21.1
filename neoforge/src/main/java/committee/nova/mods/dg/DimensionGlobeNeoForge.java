package committee.nova.mods.dg;

import committee.nova.mods.dg.client.render.GlobeBlockEntityRenderer;
import committee.nova.mods.dg.common.net.NeoForgeGlobeSectionManagerServer;
import committee.nova.mods.dg.utils.NeoForgeDimensionHelper;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

import static committee.nova.mods.dg.ModRegistries.*;

/**
 * Main mod entrypoint for NeoForge 1.21.1.
 *
 * Key differences from Forge 1.20.1:
 *  - IEventBus is injected into the constructor instead of obtained via
 *    FMLJavaModLoadingContext.get().getModEventBus().
 *  - @Mod no longer accepts a bus parameter; use @EventBusSubscriber separately.
 */
@Mod(Constants.MOD_ID)
public class DimensionGlobeNeoForge {

    public DimensionGlobeNeoForge(IEventBus modBus) {
        CommonClass.managerServer = new NeoForgeGlobeSectionManagerServer();
        CommonClass.dimensionHelper = new NeoForgeDimensionHelper();

        modBus.addListener(this::commonSetUp);

        // Client-side setup is guarded by dist check; the listener is still
        // registered on the shared modBus — NeoForge filters by dist at runtime.
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modBus.addListener(this::clientSetUp);
        }

        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        TABS.register(modBus);
        SERIALIZERS.register(modBus);
        CHUNK_GENERATOR.register(modBus);

        // Register payload types for the custom packet system.
        // Must happen during mod construction so they are registered before
        // any network traffic.  See NeoNetworkInit for the actual registration.
        NeoNetworkInit.register(modBus);
    }

    private void commonSetUp(FMLCommonSetupEvent event) {
        CommonClass.globeBlock = globeBlock1.get();
        CommonClass.globeBlockItem = globeBlockItem1.get();
        CommonClass.globeBlockEntityType = globeBlockEntityType1.get();
        CommonClass.globeItemGroup = globeItemGroup1.get();
    }

    private void clientSetUp(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
            BlockEntityRenderers.register(CommonClass.globeBlockEntityType, GlobeBlockEntityRenderer::new)
        );
    }
}
