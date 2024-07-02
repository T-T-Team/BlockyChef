package tnt.tntlib.api.date;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ZonedClockService implements ClockService {

    private final Clock clock;

    public ZonedClockService(ZoneId zone) {
        this.clock = Clock.system(zone);
    }

    @Override
    public ZonedDateTime now() {
        return ZonedDateTime.now(clock);
    }

    @Override
    public Clock clock() {
        return clock;
    }

    @Override
    public ZonedDateTime apply(ZonedDateTime dateTime) {
        return dateTime.withZoneSameInstant(clock.getZone());
    }
}
