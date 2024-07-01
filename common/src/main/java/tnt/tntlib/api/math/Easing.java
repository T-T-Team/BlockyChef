package tnt.tntlib.api.math;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import tnt.tntlib.api.functional.EasingFunction;

public enum Easing implements EasingFunction {

    LINEAR(FloatUnaryOperator.identity()),
    EASE_IN_SINE(EasingFunction.EASE_IN_SINE),
    EASE_OUT_SINE(EasingFunction.EASE_OUT_SINE),
    EASE_IN_OUT_SINE(EasingFunction.EASE_IN_OUT_SINE);

    private final FloatUnaryOperator operator;

    Easing(FloatUnaryOperator operator) {
        this.operator = operator;
    }

    @Override
    public float apply(float x) {
        return operator.apply(x);
    }
}
