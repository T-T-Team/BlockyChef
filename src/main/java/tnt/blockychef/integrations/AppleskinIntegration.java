package tnt.blockychef.integrations;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import squeek.appleskin.ModConfig;

public final class AppleskinIntegration implements ModIntegrationLayer {

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setup(FMLClientSetupEvent event) {

    }

    @Override
    public void setup(FMLCommonSetupEvent event) {
    }

    @Override
    public float getMaxHudAlphaForHydrationOverlay() {
        return ModConfig.MAX_HUD_OVERLAY_FLASH_ALPHA.get().floatValue();
    }
}
