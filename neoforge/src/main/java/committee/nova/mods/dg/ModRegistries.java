package committee.nova.mods.dg;

import com.mojang.serialization.MapCodec;
import committee.nova.mods.dg.common.block.GlobeBlock;
import committee.nova.mods.dg.common.crafting.GlobeCraftingRecipe;
import committee.nova.mods.dg.common.item.NeoForgeGlobeBlockItem;
import committee.nova.mods.dg.common.tile.GlobeBlockEntity;
import committee.nova.mods.dg.common.world.VoidChunkGenerator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * ModRegistries for NeoForge 1.21.1.
 *
 * Key differences from Forge 1.20.1:
 *  - Import path: net.neoforged.neoforge.registries.DeferredRegister
 *  - ForgeRegistries → NeoForgeRegistries for NeoForge-specific registries.
 *  - Standard vanilla registries (BLOCKS, ITEMS, etc.) use Registries.* keys
 *    directly with DeferredRegister.create(Registries.BLOCKS, MOD_ID) — this
 *    was already valid in Forge 1.20.1 and is the preferred style in NeoForge.
 *  - @Mod.EventBusSubscriber/@SubscribeEvent self-registration pattern is
 *    removed; registration is done explicitly in DimensionGlobeNeoForge via
 *    the injected IEventBus.
 */
public class ModRegistries {

    // Standard vanilla-backed registries — use Registries.* keys directly.
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, Constants.MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, Constants.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Constants.MOD_ID);

    // ChunkGenerator codec registry — still accessed via Registries.CHUNK_GENERATOR
    // in NeoForge 1.21.1 (same as Forge 1.20.1).
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATOR =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, Constants.MOD_ID);

    public static final DeferredHolder<Block, GlobeBlock> globeBlock1 =
            BLOCKS.register("globe", GlobeBlock::new);

    public static final DeferredHolder<Item, NeoForgeGlobeBlockItem> globeBlockItem1 =
            ITEMS.register("globe", () -> new NeoForgeGlobeBlockItem(globeBlock1.get(), new Item.Properties()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GlobeBlockEntity>> globeBlockEntityType1 =
            blockEntity("globe", GlobeBlockEntity::new, () -> new Block[]{globeBlock1.get()});

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GlobeCraftingRecipe>> globeCrafting1 =
            serializer("globe", () -> new SimpleCraftingRecipeSerializer<>(GlobeCraftingRecipe::new));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> globeItemGroup1 = TABS.register("globe_group", () ->
            CreativeModeTab.builder()
                    .title(Component.literal("Globes"))
                    .icon(() -> new ItemStack(globeBlockItem1.get()))
                    .displayItems((context, entries) -> {
                        for (Block block : BuiltInRegistries.BLOCK) {
                            if (block.defaultBlockState().is(CommonClass.BASE_BLOCK_TAG)) {
                                entries.accept(globeBlockItem1.get().getWithBase(block));
                            }
                        }
                        entries.accept(globeBlockItem1.get());
                    })
                    .build());

    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<VoidChunkGenerator>> VOID_CHUNK =
            CHUNK_GENERATOR.register("globe", () -> VoidChunkGenerator.CODEC);

    // ---------- helpers ----------

    public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> blockEntity(
            String name,
            BlockEntityType.BlockEntitySupplier<T> tile,
            Supplier<Block[]> blocks) {
        return BLOCK_ENTITIES.register(name,
                () -> BlockEntityType.Builder.of(tile, blocks.get()).build(null));
    }

    public static <T extends CustomRecipe> DeferredHolder<RecipeSerializer<?>, RecipeSerializer<T>> serializer(
            String name,
            Supplier<RecipeSerializer<T>> serializer) {
        return SERIALIZERS.register(name, serializer);
    }
}
