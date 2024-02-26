package tnt.tntlib.api.math;

import tnt.tntlib.api.functional.EasingFunction;

public final class Interpolation {

    public static float linear(float current, float prev, float partial) {
        return prev + (current - prev) * partial;
    }

    public static float ease(float f, EasingFunction easingFunction) {
        return easingFunction.apply(f);
    }
}
