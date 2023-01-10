package tnt.blockychef.integrations;

import net.minecraftforge.fml.ModList;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Integrations {

    public static final String APPLESKIN = "appleskin";

    private static final Map<String, ModIntegrationLayer> implementationLayers = new HashMap<>();

    static {
        registerIntegrationLayer(APPLESKIN, AppleskinIntegration::new);
    }

    public static void accept(Consumer<ModIntegrationLayer> consumer) {
        implementationLayers.values().forEach(consumer);
    }

    public static boolean exists(String modId) {
        return implementationLayers.containsKey(modId);
    }

    private static void registerIntegrationLayer(String modId, Supplier<ModIntegrationLayer> supplier) {
        if (ModList.get().isLoaded(modId)) {
            implementationLayers.put(modId, supplier.get());
        }
    }
}
