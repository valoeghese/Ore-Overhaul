package tk.valoeghese.oreoverhaul;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import tk.valoeghese.oreoverhaul.util.OpenSimplexNoise;

public final class Caches {
	private Caches() {
		// NO-OP
	}

	private static long cachedSeed = 0;
	private static BlockPos cachedPos;
	private static OpenSimplexNoise generator;
	private static double[] noise = new double[8];

	public static ConfiguredDecorator<?> rangeDecorator(long seed, BlockPos pos, RangeDecoratorConfig config, int magic) {
		initialise(seed, pos);

		int count = config.count;
		return Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(
				(int) (noise[magic & 7] * count + (count < 15 ? 0 : count - 15)),
				config.bottomOffset,
				config.topOffset,
				config.maximum));
	}

	public static ConfiguredDecorator<?> depthAverageDecorator(long seed, BlockPos pos, CountDepthDecoratorConfig config, int magic) {
		initialise(seed, pos);

		int count = config.count;
		return Decorator.COUNT_DEPTH_AVERAGE.configure(new CountDepthDecoratorConfig(
				(int) (noise[magic & 7] * count + (count < 15 ? 0 : count - 15)),
				config.baseline,
				config.spread));
	}

	private static void initialise(long seed, BlockPos pos) {
		boolean regen = false;

		if (generator == null || seed != cachedSeed) {
			generator = new OpenSimplexNoise(new Random(seed));
			regen = true;
		}

		if (pos != cachedPos) {
			regen = true;
		}

		if (regen) {
			double x = 0.0011 * pos.getX();
			double z = 0.0011 * pos.getZ();

			for (int i = 0; i < 8; ++i) {
				//noise[i] = 0.8 * generator.sample(x, z) + 0.5; LESS COMMON, [-0.3,1.3]
				noise[i] = 0.95 * generator.sample(x, z) + 0.65; // [-0.3,1.6]
				x += 0.23;
			}
		}
	}
}
