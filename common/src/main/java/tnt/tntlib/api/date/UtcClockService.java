package tnt.tntlib.api.date;

import java.time.ZoneOffset;

public class UtcClockService extends ZonedClockService {

    public UtcClockService() {
        super(ZoneOffset.UTC);
    }
}
