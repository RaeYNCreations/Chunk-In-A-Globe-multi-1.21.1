package committee.nova.mods.dg.common.item;

import committee.nova.mods.dg.CommonClass;
import committee.nova.mods.dg.common.tile.GlobeBlockEntity;
import committee.nova.mods.dg.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlobeBlockItem extends BlockItem {

    public GlobeBlockItem(Block block, Properties settings) {
        super(block, settings);
    }

    // 1.21.1: ItemStack NBT replaced by DataComponents.
    // We store globe data in DataComponents.CUSTOM_DATA (a CompoundTag wrapper).
    public ItemStack getWithBase(Block base) {
        ResourceLocation identifier = Services.PLATFORM.getKeyByBlock(base);
        ItemStack stack = new ItemStack(this);
        CompoundTag tag = new CompoundTag();
        tag.putString("base_block", identifier.toString());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    /** Read the CustomData tag, or empty CompoundTag if absent. */
    public static CompoundTag getTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null ? data.copyTag() : new CompoundTag();
    }

    public static boolean hasKey(ItemStack stack, String key) {
        return getTag(stack).contains(key);
    }

    @Override
    public @NotNull InteractionResult place(BlockPlaceContext context) {
        if (context.getPlayer().level().dimension().equals(CommonClass.globeDimension)) {
            if (!context.getPlayer().level().isClientSide) {
                context.getPlayer().displayClientMessage(
                        Component.translatable("globedimension.block.error"), false);
            }
            return InteractionResult.FAIL;
        }
        return super.place(context);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(@NotNull BlockPos pos, @NotNull Level world,
                                                 @Nullable Player player, @NotNull ItemStack stack,
                                                 @NotNull BlockState state) {
        CompoundTag tag = getTag(stack);
        if (tag.contains("base_block")) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GlobeBlockEntity gbe) {
                ResourceLocation identifier = ResourceLocation.parse(tag.getString("base_block"));
                gbe.setBaseBlock(Services.PLATFORM.getBlockByKey(identifier));
                if (tag.contains("globe_id")) {
                    gbe.setGlobeID(tag.getInt("globe_id"));
                }
            }
        }
        return super.updateCustomBlockEntityTag(pos, world, player, stack, state);
    }
}
