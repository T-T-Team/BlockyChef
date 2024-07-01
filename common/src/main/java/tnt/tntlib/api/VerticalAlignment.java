package tnt.tntlib.api;

public enum VerticalAlignment {

    TOP(Alignment.MIN),
    CENTER(Alignment.MID),
    BOTTOM(Alignment.MAX);

    private final Alignment alignment;

    VerticalAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public float align(float y, float height, float size) {
        return alignment.align(y, height, size);
    }

    public float adjustPadding(float paddingVal) {
        return this == TOP ? paddingVal : this == BOTTOM ? -paddingVal : 0;
    }
}
