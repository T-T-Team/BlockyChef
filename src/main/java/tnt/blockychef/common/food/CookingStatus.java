package tnt.blockychef.common.food;

public enum CookingStatus {
    NONE,
    COOKING,
    BURNING;

    public boolean isCooking() {
        return this == COOKING;
    }

    public boolean isBurning() {
        return this == BURNING;
    }
}
