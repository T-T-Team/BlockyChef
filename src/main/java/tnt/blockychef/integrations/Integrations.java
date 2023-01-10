package tnt.blockychef.integrations;

import net.minecraftforge.fml.ModList;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Integrations {

    private static final Map<String, ModIntegrationLayer> implementationLayers = new HashMap<>();

    static {
        registerIntegrationLayer("appleskin", AppleskinIntegration::new);
    }

    public static void accept(Consumer<ModIntegrationLayer> consumer) {
        implementationLayers.values().forEach(consumer);
    }

    private static void registerIntegrationLayer(String modId, Supplier<ModIntegrationLayer> supplier) {
        if (ModList.get().isLoaded(modId)) {
            implementationLayers.put(modId, supplier.get());
        }
    }
}
