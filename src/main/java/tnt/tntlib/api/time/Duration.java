package tnt.tntlib.api.time;

import tnt.tntlib.core.math.DurationImpl;

public interface Duration extends TickTime {

    Duration add(Duration other);

    Duration add(TimeUnit unit, long value);

    Duration addAll(TimeValue... values);

    String format(TimeFormat format);

    static Duration ticks(long ticks) {
        return new DurationImpl(TimeUnit.TICKS, ticks);
    }

    static Duration seconds(long seconds) {
        return new DurationImpl(TimeUnit.SECONDS, seconds);
    }

    static Duration minutes(long minutes) {
        return new DurationImpl(TimeUnit.MINUTES, minutes);
    }

    static Duration hours(long hours) {
        return new DurationImpl(TimeUnit.HOURS, hours);
    }

    static Duration days(long days) {
        return new DurationImpl(TimeUnit.DAYS, days);
    }

    static Duration weeks(long weeks) {
        return new DurationImpl(TimeUnit.WEEKS, weeks);
    }

    static Duration months(long months) {
        return new DurationImpl(TimeUnit.MONTHS, months);
    }

    static Duration years(long years) {
        return new DurationImpl(TimeUnit.YEARS, years);
    }

    static Duration combined(TimeValue... values) {
        return new DurationImpl(values);
    }
}
