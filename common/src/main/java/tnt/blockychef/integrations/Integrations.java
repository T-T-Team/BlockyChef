package tnt.blockychef.integrations;

import net.minecraftforge.fml.ModList;
import tnt.blockychef.client.BlockyChefClient;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Integrations {

    public static final String APPLESKIN = "appleskin";

    private static final Map<String, ModIntegrationLayer> implementationLayers = new HashMap<>();
    private static float cachedHydrationOverlayValue = -1.0F;

    static {
        registerIntegrationLayer(APPLESKIN, AppleskinIntegration::new);
    }

    public static void accept(Consumer<ModIntegrationLayer> consumer) {
        implementationLayers.values().forEach(consumer);
    }

    public static boolean exists(String modId) {
        return implementationLayers.containsKey(modId);
    }

    public static boolean shouldExpandFoodTooltips() {
        return exists(APPLESKIN);
    }

    public static boolean shouldRenderFancyOverlay() {
        return exists(APPLESKIN) || BlockyChefClient.CLIENT.config.forceFancyThirstOverlay;
    }

    public static float getAlphaForHudHydrationOverlay() {
        if (cachedHydrationOverlayValue < 0) {
            float val = (float) implementationLayers.values().stream()
                    .mapToDouble(ModIntegrationLayer::getMaxHudAlphaForHydrationOverlay)
                    .max().orElse(0.65F);
            return val < 0.0F ? 0.65F : val;
        }
        return cachedHydrationOverlayValue;
    }

    private static void registerIntegrationLayer(String modId, Supplier<ModIntegrationLayer> supplier) {
        if (ModList.get().isLoaded(modId)) {
            implementationLayers.put(modId, supplier.get());
        }
    }
}
