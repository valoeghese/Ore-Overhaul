package tk.valoeghese.oreoverhaul.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeature;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import tk.valoeghese.oreoverhaul.Caches;

@Mixin(DecoratedFeature.class)
public class MixinDecoratedFeature {
	@Redirect(
			at = @At(value = "INVOKE", target = "net/minecraft/world/gen/decorator/ConfiguredDecorator.generate(Lnet/minecraft/world/IWorld;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/ConfiguredFeature;)Z"),
			method = "generate(Lnet/minecraft/world/IWorld;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/DecoratedFeatureConfig;)Z"
			)
	public boolean onGenerate(ConfiguredDecorator<?> instance, IWorld world, StructureAccessor structureAccessor, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, ConfiguredFeature<?, ?> configuredFeature) {
		if (configuredFeature.feature instanceof OreFeature) {
			DecoratorConfig dcfg = instance.config;

			if (dcfg instanceof RangeDecoratorConfig) {
				return Caches.rangeDecorator(
						world.getSeed(),
						blockPos,
						(RangeDecoratorConfig) dcfg,
						Registry.BLOCK.getRawId(((OreFeatureConfig) configuredFeature.config).state.getBlock())
						).generate(world, structureAccessor, chunkGenerator, random, blockPos, configuredFeature);
			} else if (dcfg instanceof CountDepthDecoratorConfig) {
				return Caches.depthAverageDecorator(
						world.getSeed(),
						blockPos,
						(CountDepthDecoratorConfig) dcfg,
						Registry.BLOCK.getRawId(((OreFeatureConfig) configuredFeature.config).state.getBlock())
						).generate(world, structureAccessor, chunkGenerator, random, blockPos, configuredFeature);
			}
		}
		return instance.generate(world, structureAccessor, chunkGenerator, random, blockPos, configuredFeature);
	}
}
