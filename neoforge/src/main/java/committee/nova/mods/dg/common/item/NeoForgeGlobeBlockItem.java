package committee.nova.mods.dg.common.item;

import committee.nova.mods.dg.client.NeoForgeGlobeItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * NeoForge 1.21.1 item subclass for the globe item.
 *
 * IClientItemExtensions is unchanged between Forge 1.20.1 and NeoForge 1.21.1.
 * Only the import path changes:
 *   Old: net.minecraftforge.client.extensions.common.IClientItemExtensions
 *   New: net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
 */
public class NeoForgeGlobeBlockItem extends GlobeBlockItem {

    public NeoForgeGlobeBlockItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new NeoForgeGlobeItemRenderer(
                        Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                        Minecraft.getInstance().getEntityModels());
            }
        });
    }
}
