package committee.nova.mods.dg.common.block;

import com.mojang.serialization.MapCodec;
import committee.nova.mods.dg.CommonClass;
import committee.nova.mods.dg.common.item.GlobeBlockItem;
import committee.nova.mods.dg.common.tile.GlobeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlobeBlock extends BaseEntityBlock {

    public static final MapCodec<GlobeBlock> CODEC = simpleCodec(p -> new GlobeBlock());

    public GlobeBlock() {
        super(Properties.of().noOcclusion());
    }

    @Override
    public @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GlobeBlockEntity(pos, state);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level world,
                                                      @NotNull BlockPos pos, @NotNull Player player,
                                                      @NotNull BlockHitResult hit) {
        if (!world.isClientSide) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof GlobeBlockEntity gbe) {
                gbe.transportPlayer((ServerPlayer) player);
            }
        }
        return InteractionResult.SUCCESS;
    }

    // 1.21.1: playerWillDestroy now returns BlockState.
    @Override
    public @NotNull BlockState playerWillDestroy(Level world, @NotNull BlockPos pos,
                                                  @NotNull BlockState state, @NotNull Player player) {
        popResource(world, pos, getDroppedStack(world, pos));
        return super.playerWillDestroy(world, pos, state, player);
    }

    // 1.21.1: Block#getDrops is final — can't override. Drop logic is handled via playerWillDestroy above.
    // getCloneItemStack: NeoForge's IBlockExtension uses LevelReader (not BlockGetter) in 1.21.1.
    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull LevelReader world, @NotNull BlockPos pos,
                                                 @NotNull BlockState state) {
        return getDroppedStack(world, pos);
    }

    private ItemStack getDroppedStack(BlockGetter world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof GlobeBlockEntity gbe) {
            ItemStack stack = CommonClass.globeBlockItem.getWithBase(gbe.getBaseBlock());
            CustomData existing = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag tag = existing.copyTag();
            tag.putInt("globe_id", gbe.getGlobeID());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level world,
                                                                    @NotNull BlockState state,
                                                                    @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, CommonClass.globeBlockEntityType, GlobeBlockEntity::tick);
    }
}
