package tnt.tntlib.api;

import java.util.function.IntConsumer;

public final class UiHelper {

    public static boolean handleMouseScrolled(double amount, int currScrollIndex, int displaySize, int dataSize, IntConsumer onScroll) {
        int next = currScrollIndex - (int) amount;
        if (next >= 0 && next <= dataSize - displaySize) {
            onScroll.accept(next);
            return true;
        }
        if (currScrollIndex < 0 || currScrollIndex > dataSize - displaySize) {
            onScroll.accept(0);
            return true;
        }
        return false;
    }

    private UiHelper() {}
}
