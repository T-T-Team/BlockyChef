package tnt.tntlib.api.date;

import java.time.Clock;
import java.time.ZonedDateTime;

public interface ClockService {

    ZonedDateTime now();

    Clock clock();

    ZonedDateTime apply(ZonedDateTime dateTime);
}
