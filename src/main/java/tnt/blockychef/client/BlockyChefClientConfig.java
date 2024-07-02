package tnt.blockychef.client;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import squeek.appleskin.helpers.KeyHelper;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.heat.HeatValues;

@Config(id = "blockychef-client", filename = "blockychef/blockychef-client", group = BlockyChef.MODID)
public final class BlockyChefClientConfig {

    @Configurable
    @Configurable.Comment("Will force AppleSkin-like thirst overlay rendering")
    public boolean forceFancyThirstOverlay = false;

    @Configurable
    @Configurable.DecimalRange(min = 0.1F, max = HeatValues.MAX_TEMPERATURE)
    @Configurable.Comment("Temperature step size for UI buttons")
    public float temperatureStepSize = 0.5F;

    @Configurable
    @Configurable.DecimalRange(min = 0.1F, max = HeatValues.MAX_TEMPERATURE)
    @Configurable.Comment("Temperature step size for UI buttons while holding SHIFT key")
    public float shiftTemperatureStepSize = 0.25F;

    @Configurable
    @Configurable.DecimalRange(min = 0.1F, max = HeatValues.MAX_TEMPERATURE)
    @Configurable.Comment("Temperature step size for UI buttons while holding CTRL key")
    public float ctrlTemperatureStepSize = 0.1F;

    @OnlyIn(Dist.CLIENT)
    public float getTemperatureStepAmount() {
        return KeyHelper.isCtrlKeyDown() ? ctrlTemperatureStepSize : KeyHelper.isShiftKeyDown() ? shiftTemperatureStepSize : temperatureStepSize;
    }
}
