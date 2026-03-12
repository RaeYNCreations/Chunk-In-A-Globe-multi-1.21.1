package committee.nova.mods.dg.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VoidChunkGenerator extends ChunkGenerator {

	// 1.21.1: codec() now returns MapCodec, not Codec.
	public static final MapCodec<VoidChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
			instance.group(RegistryOps.retrieveElement(Biomes.PLAINS))
					.apply(instance, instance.stable(VoidChunkGenerator::new)));

	public VoidChunkGenerator(Holder<Biome> biomeEntry) {
		super(new FixedBiomeSource(biomeEntry));
	}

	@Override
	protected @NotNull MapCodec<? extends ChunkGenerator> codec() {
		return CODEC;
	}

	@Override
	public void applyCarvers(@NotNull WorldGenRegion region, long seed, @NotNull RandomState randomState,
							 @NotNull BiomeManager biomeManager, @NotNull StructureManager structureManager,
							 @NotNull ChunkAccess chunk, GenerationStep.@NotNull Carving carving) {}

	@Override
	public void buildSurface(@NotNull WorldGenRegion region, @NotNull StructureManager structureManager,
							 @NotNull RandomState randomState, @NotNull ChunkAccess chunk) {}

	@Override
	public void spawnOriginalMobs(@NotNull WorldGenRegion region) {}

	@Override
	public int getGenDepth() { return 384; }

	// 1.21.1: fillFromNoise no longer takes Executor or Blender.
	@Override
	public @NotNull CompletableFuture<ChunkAccess> fillFromNoise(@NotNull Blender blender,
																 @NotNull RandomState randomState,
																 @NotNull StructureManager structureManager,
																 @NotNull ChunkAccess chunk) {
		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public int getSeaLevel() { return 0; }

	@Override
	public int getMinY() { return 0; }

	@Override
	public int getBaseHeight(int x, int z, Heightmap.@NotNull Types types,
							 @NotNull LevelHeightAccessor heightAccessor,
							 @NotNull RandomState randomState) { return 0; }

	@Override
	public @NotNull NoiseColumn getBaseColumn(int x, int z,
											  @NotNull LevelHeightAccessor heightAccessor,
											  @NotNull RandomState randomState) {
		return new NoiseColumn(0, new BlockState[0]);
	}

	@Override
	public void addDebugScreenInfo(@NotNull List<String> list, @NotNull RandomState randomState,
								   @NotNull BlockPos pos) {}
}
