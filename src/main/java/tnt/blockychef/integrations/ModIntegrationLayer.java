package tnt.blockychef.integrations;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public interface ModIntegrationLayer {

    @OnlyIn(Dist.CLIENT)
    void setup(FMLClientSetupEvent event);

    void setup(FMLCommonSetupEvent event);

    default float getMaxHudAlphaForHydrationOverlay() {
        return -1.0F;
    }
}
