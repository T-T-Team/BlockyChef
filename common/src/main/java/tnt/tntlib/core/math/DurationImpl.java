package tnt.tntlib.core.math;

import tnt.tntlib.api.time.Duration;
import tnt.tntlib.api.time.TimeFormat;
import tnt.tntlib.api.time.TimeUnit;
import tnt.tntlib.api.time.TimeValue;

public final class DurationImpl implements Duration {

    private final long value;

    public DurationImpl(TimeUnit unit, long value) {
        this.value = unit.transformToTicks(value);
    }

    public DurationImpl(TimeValue... values) {
        long total = 0L;
        for (TimeValue value : values) {
            total += value.unit().transformToTicks(value.value());
        }
        this.value = total;
    }

    private DurationImpl(long value) {
        this.value = value;
    }

    @Override
    public long value() {
        return value;
    }

    @Override
    public Duration add(Duration other) {
        return new DurationImpl(value + other.value());
    }

    @Override
    public Duration addAll(TimeValue... values) {
        return add(new DurationImpl(values));
    }

    @Override
    public Duration add(TimeUnit unit, long value) {
        return new DurationImpl(this.value + unit.transformToTicks(value));
    }

    @Override
    public String format(TimeFormat format) {
        return format.format(this);
    }
}
