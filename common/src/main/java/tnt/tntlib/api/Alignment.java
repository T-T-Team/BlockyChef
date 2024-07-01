package tnt.tntlib.api;

@FunctionalInterface
public interface Alignment {

    Alignment MIN = (from, size, elementSize) -> from;
    Alignment MID = (from, size, elementSize) -> from + (size - elementSize) / 2.0F;
    Alignment MAX = (from, size, elementSize) -> from + size - elementSize;

    float align(float from, float size, float elementSize);
}
