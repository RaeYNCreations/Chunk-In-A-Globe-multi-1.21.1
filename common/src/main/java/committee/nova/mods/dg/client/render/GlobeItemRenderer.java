package committee.nova.mods.dg.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import committee.nova.mods.dg.common.item.GlobeBlockItem;
import committee.nova.mods.dg.platform.Services;
import committee.nova.mods.dg.utils.GlobeSectionManagerClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.renderer.MultiBufferSource;

public class GlobeItemRenderer {
    public static void render(ItemStack stack, PoseStack matrix,
                               MultiBufferSource vertexConsumerProvider, int light, int overlay) {
        Block baseBlock = Blocks.BLUE_CONCRETE;
        CompoundTag tag = GlobeBlockItem.getTag(stack);
        if (tag.contains("base_block")) {
            ResourceLocation blockID = ResourceLocation.parse(tag.getString("base_block"));
            baseBlock = Services.PLATFORM.getBlockByKey(blockID);
        }
        GlobeBlockEntityRenderer.renderBase(baseBlock, matrix, vertexConsumerProvider, light, overlay);
        if (tag.contains("globe_id")) {
            int globeId = tag.getInt("globe_id");
            GlobeBlockEntityRenderer.renderGlobe(false, globeId, matrix, vertexConsumerProvider, light);
            GlobeSectionManagerClient.requestGlobeUpdate(globeId);
        }
    }
}
