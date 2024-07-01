package tnt.tntlib.api;

import tnt.tntlib.api.serialization.SerializationUtils;

import java.util.function.BiPredicate;

public record SimpleVersion(int major, int minor, int patch) {

    public static SimpleVersion parse(int version) {
        int patch = version & 0xFFF;
        int minor = (version >> 12) & 0xFFF;
        int major = (version >> 24) & 0xFF;
        return new SimpleVersion(major, minor, patch);
    }

    public static SimpleVersion parseString(String versionString) {
        String[] versions = versionString.split("\\.", 3);
        int major = versions.length > 0 ? SerializationUtils.tryParseInt(versions[0], 0) : 0;
        int minor = versions.length > 1 ? SerializationUtils.tryParseInt(versions[1], 0) : 0;
        int patch = versions.length > 2 ? SerializationUtils.tryParseInt(versions[2], 0) : 0;
        return new SimpleVersion(major, minor, patch);
    }

    public int encode() {
        int encoded = patch;
        encoded |= (minor << 12);
        encoded |= (major << 24);
        return encoded;
    }

    public boolean matches(SimpleVersion other, ComparationType comparationType) {
        return comparationType.comparator.test(this, other);
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }

    public enum ComparationType {

        ALL_MATCH((v1, v2) -> v1.encode() == v2.encode()),
        MAJOR_MATCH((v1, v2) -> v1.major == v2.major),
        MINOR_MATCH((v1, v2) -> v1.major == v2.major && v1.minor == v2.minor);

        private final BiPredicate<SimpleVersion, SimpleVersion> comparator;

        ComparationType(BiPredicate<SimpleVersion, SimpleVersion> comparator) {
            this.comparator = comparator;
        }
    }
}
