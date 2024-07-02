package tnt.tntlib.api.date;

import java.time.ZoneId;

public class LocalClockService extends ZonedClockService {

    public LocalClockService() {
        super(ZoneId.systemDefault());
    }
}
