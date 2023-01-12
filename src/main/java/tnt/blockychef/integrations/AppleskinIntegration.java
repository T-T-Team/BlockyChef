package tnt.blockychef.integrations;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import squeek.appleskin.ModConfig;
import squeek.appleskin.api.event.FoodValuesEvent;

public final class AppleskinIntegration implements ModIntegrationLayer {

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setup(FMLClientSetupEvent event) {

    }

    @Override
    public void setup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(this::gatherModifiedFoodValues);
    }

    @Override
    public float getMaxHudAlphaForHydrationOverlay() {
        return ModConfig.MAX_HUD_OVERLAY_FLASH_ALPHA.get().floatValue();
    }

    private void gatherModifiedFoodValues(FoodValuesEvent event) {
        // TODO adjust based on food stats
    }
}
