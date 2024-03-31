package tnt.tntlib.api;

import net.minecraft.util.RandomSource;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.function.Consumer;

public class TNTUtils {

    private static Boolean DEV_ENV;

    public static boolean isDevelopmentEnvironment() {
        if (DEV_ENV == null) {
            DEV_ENV = !FMLEnvironment.production;
        }
        return DEV_ENV;
    }

    @SuppressWarnings("unchecked")
    public static <R, I> R unsafeCast(I input) {
        return (R) input;
    }

    public static <T> T createInit(T obj, Consumer<T> init) {
        init.accept(obj);
        return obj;
    }

    public static double randomRange(RandomSource random, double modifier) {
        return (random.nextDouble() - random.nextDouble()) * modifier;
    }

    public static double randomRange(RandomSource random) {
        return randomRange(random, 1.0);
    }
}
