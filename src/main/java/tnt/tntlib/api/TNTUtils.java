package tnt.tntlib.api;

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
}
