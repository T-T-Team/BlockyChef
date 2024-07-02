package tnt.tntlib.api.time;

public interface TickTime {

    long value();

    default int intValue() {
        return (int) value();
    }
}
