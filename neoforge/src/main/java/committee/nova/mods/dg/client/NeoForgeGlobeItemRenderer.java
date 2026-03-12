package committee.nova.mods.dg.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static committee.nova.mods.dg.client.render.GlobeItemRenderer.render;

/**
 * NeoForge 1.21.1 — API identical to Forge 1.20.1 for BlockEntityWithoutLevelRenderer.
 * Only class/file name changed for clarity.
 */
public class NeoForgeGlobeItemRenderer extends BlockEntityWithoutLevelRenderer {

    public NeoForgeGlobeItemRenderer(BlockEntityRenderDispatcher renderDispatcher,
                                     EntityModelSet modelSet) {
        super(renderDispatcher, modelSet);
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack,
                             @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack,
                             @NotNull MultiBufferSource source,
                             int light, int overlay) {
        render(stack, poseStack, source, light, overlay);
    }
}
