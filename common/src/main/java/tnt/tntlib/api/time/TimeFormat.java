package tnt.tntlib.api.time;

import joptsimple.internal.Strings;
import tnt.tntlib.api.ArrayUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.*;

public final class TimeFormat {

    public static final Predicate<TimeUnit> HHMMSS_FILTER = TimeUnit::isClockUnit;
    public static final TimeFormat FULL_CLOCK = new Builder()
            .withSeparator(" ")
            .withUnitFilter(HHMMSS_FILTER)
            .withFullComponentFormatter((time, unit) -> time + " " + unit)
            .setAlwaysSkipEmptyValues()
            .buildFormat();
    public static final TimeFormat PARTIAL_CLOCK = new Builder()
            .withUnitFilter(HHMMSS_FILTER)
            .withUnitIdentifierFormatter((time, unit) -> time + unit, true)
            .buildFormat();
    public static final TimeFormat CLOCK = new Builder()
            .withSeparator(":")
            .withUnitFilter(HHMMSS_FILTER)
            .withClockLikeFormatter()
            .buildFormat();

    private final String unitSeparator;
    private final Predicate<TimeUnit> unitFilter;
    private final Function<TimeValue, String> unitFormatter;
    private final boolean alwaysSkipEmptyValues;

    private TimeFormat(String unitSeparator, Predicate<TimeUnit> unitFilter, Function<TimeValue, String> unitFormatter, boolean alwaysSkipEmptyValues) {
        this.unitSeparator = unitSeparator;
        this.unitFilter = unitFilter;
        this.unitFormatter = unitFormatter;
        this.alwaysSkipEmptyValues = alwaysSkipEmptyValues;
    }

    private static String pad(String valueString, int size) {
        int diff = size - valueString.length();
        if (diff > 0) {
            String prefix = Strings.repeat('0', diff);
            return prefix + valueString;
        }
        return valueString;
    }

    public String format(Duration duration) {
        StringBuilder builder = new StringBuilder();
        long totalValue = duration.value();
        boolean skipping = true;
        TimeUnit[] units = Arrays.stream(TimeUnit.values()).filter(unitFilter).toArray(TimeUnit[]::new);
        for (int i = units.length - 1; i >= 0; i--) {
            TimeUnit unit = units[i];
            long count = unit.div(totalValue);
            if (count == 0 && alwaysSkipEmptyValues) {
                continue;
            }
            if (!skipping || count > 0) {
                skipping = false;
                boolean isLast = i == 0;
                builder.append(unitFormatter.apply(new TimeValue(unit, count)));
                if (!isLast) {
                    builder.append(unitSeparator);
                }
                totalValue = unit.mod(totalValue);
            }
        }
        return builder.toString();
    }

    public static final class Builder {

        private String unitSeparator = "";
        private Function<TimeValue, String> unitFormatter = tv -> String.valueOf(tv.value()) + tv.unit().getIdentifier();
        private Predicate<TimeUnit> unitFilter = tu -> true;
        private boolean alwaysSkipEmptyValues = false;

        public Builder withSeparator(String separator) {
            this.unitSeparator = Objects.requireNonNull(separator);
            return this;
        }

        public Builder withUnitFilter(Predicate<TimeUnit> unitFilter) {
            this.unitFilter = unitFilter;
            return this;
        }

        public Builder withFormatter(Function<TimeValue, String> unitFormatter) {
            this.unitFormatter = unitFormatter;
            return this;
        }

        public Builder withFullComponentFormatter(BinaryOperator<String> formatter, boolean padding) {
            return this.withFormatter(tv -> {
                TimeUnit unit = tv.unit();
                String baseValue = String.valueOf(tv.value());
                if (padding && unit.shouldBePadded()) {
                    baseValue = pad(baseValue, 2);
                }
                return formatter.apply(baseValue, unit.getUnitName().getString());
            });
        }

        public Builder withFullComponentFormatter(BinaryOperator<String> formatter) {
            return this.withFullComponentFormatter(formatter, false);
        }

        public Builder withUnitIdentifierFormatter(BinaryOperator<String> formatter, boolean padding) {
            return this.withFormatter(tv -> {
                TimeUnit unit = tv.unit();
                String baseValue = String.valueOf(tv.value());
                if (padding && unit.shouldBePadded()) {
                    baseValue = pad(baseValue, 2);
                }
                return formatter.apply(baseValue, String.valueOf(unit.getIdentifier()));
            });
        }

        public Builder withClockLikeFormatter() {
            return this.withFormatter(tv -> {
                TimeUnit unit = tv.unit();
                String baseValue = String.valueOf(tv.value());
                if (unit.shouldBePadded()) {
                    baseValue = pad(baseValue, 2);
                }
                return baseValue;
            });
        }

        public Builder withUnitIdentifierFormatter(BinaryOperator<String> formatter) {
            return this.withUnitIdentifierFormatter(formatter, false);
        }

        public Builder withWhitelistUnitFilter(TimeUnit... allowedValues) {
            return this.withUnitFilter(unit -> ArrayUtils.containsElement(allowedValues, unit));
        }

        public Builder withBlacklistUnitFilter(TimeUnit... disabledValues) {
            return this.withUnitFilter(unit -> !ArrayUtils.containsElement(disabledValues, unit));
        }

        public Builder setAlwaysSkipEmptyValues() {
            this.alwaysSkipEmptyValues = true;
            return this;
        }

        public TimeFormat buildFormat() {
            return new TimeFormat(unitSeparator, unitFilter, unitFormatter, alwaysSkipEmptyValues);
        }
    }
}
