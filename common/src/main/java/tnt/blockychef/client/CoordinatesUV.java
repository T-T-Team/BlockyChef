package tnt.blockychef.client;

public record CoordinatesUV(int u1, int v1, int u2, int v2, int size) {

    public CoordinatesUV(int u1, int v1, int u2, int v2) {
        this(u1, v1, u2, v2, 256);
    }

    float rawU1() {
        return u1() / (float) size();
    }

    float rawV1() {
        return v1() / (float) size();
    }

    float rawU2() {
        return u2() / (float) size();
    }

    float rawV2() {
        return v2() / (float) size();
    }
}
