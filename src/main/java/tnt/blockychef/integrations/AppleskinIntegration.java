package tnt.blockychef.integrations;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import squeek.appleskin.ModConfig;
import squeek.appleskin.api.event.FoodValuesEvent;
import squeek.appleskin.api.food.FoodValues;
import tnt.blockychef.common.food.mastery.CookingMastery;
import tnt.blockychef.common.food.mastery.FoodQuality;

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
        ItemStack stack = event.itemStack;
        UseAnim anim = stack.getUseAnimation();
        if (anim == UseAnim.EAT) {
            FoodQuality quality = CookingMastery.getItemQuality(stack);
            if (quality != null) {
                FoodValues original = event.defaultFoodValues;
                System.out.println(original);
            }
        }
    }
}
