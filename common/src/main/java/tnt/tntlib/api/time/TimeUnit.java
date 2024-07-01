package tnt.tntlib.api.time;

import net.minecraft.network.chat.Component;

public enum TimeUnit {

    TICKS       (1L, 't'),
    SECONDS     (20L, 's', true),
    MINUTES     (1_200L, 'm', true),
    HOURS       (72_000L, 'h', true),
    DAYS        (1_728_000L, 'D'),
    WEEKS       (12_096_000L, 'W'),
    MONTHS      (51_840_000L, 'M'),
    YEARS       (630_720_000L, 'Y');

    private final long tickValue;
    private final char identifier;
    private final boolean shouldPad;
    private final Component translatedText;

    TimeUnit(long tickValue, char identifier) {
        this(tickValue, identifier, false);
    }

    TimeUnit(long tickValue, char identifier, boolean shouldPad) {
        this.tickValue = tickValue;
        this.identifier = identifier;
        this.shouldPad = shouldPad;
        this.translatedText = Component.translatable("label.time.unit." + name().toLowerCase());
    }

    public Component getUnitName() {
        return translatedText;
    }

    public long div(long ticks) {
        return ticks / tickValue;
    }

    public long mod(long ticks) {
        return ticks % tickValue;
    }

    public long transformToTicks(long value) {
        return this.tickValue * value;
    }

    public boolean shouldBePadded() {
        return shouldPad;
    }

    public char getIdentifier() {
        return identifier;
    }

    public boolean isClockUnit() {
        return this == SECONDS || this == MINUTES || this == HOURS;
    }

    public boolean isDateUnit() {
        return !isClockUnit() && this != WEEKS && this != TICKS;
    }
}
