package tnt.tntlib.api;

public enum HorizontalAlignment {

    LEFT(Alignment.MIN),
    CENTER(Alignment.MID),
    RIGHT(Alignment.MAX);

    private final Alignment alignment;

    HorizontalAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public float align(float x, float width, float size) {
        return alignment.align(x, width, size);
    }

    public float adjustPadding(float paddingVal) {
        return this == LEFT ? paddingVal : this == RIGHT ? -paddingVal : 0;
    }
}
