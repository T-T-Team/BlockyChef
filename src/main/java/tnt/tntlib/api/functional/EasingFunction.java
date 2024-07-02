package tnt.tntlib.api.functional;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.util.Mth;

@FunctionalInterface
public interface EasingFunction extends FloatUnaryOperator {

    float PI = (float) Math.PI;

    EasingFunction EASE_IN_SINE = x -> 1.0F - Mth.cos((x * PI) / 2.0F);
    EasingFunction EASE_OUT_SINE = x -> Mth.sin((x * PI) / 2.0F);
    EasingFunction EASE_IN_OUT_SINE = x -> -(Mth.cos(PI * x) - 1.0F) / 2.0F;
}
