package tnt.blockychef.util;

import net.minecraft.world.item.DyeColor;
import tnt.blockychef.common.block.entity.IndexedColorHolder;

public final class ColorHelper {

    public static void setColor(IndexedColorHolder colorHolder, int layer, DyeColor color, boolean mixPrevious) {
        int oldColor = colorHolder.getColor(layer);
        int newColor = color.getTextColor();
        if (mixPrevious) {
            newColor = mix(oldColor, color);
        }
        colorHolder.setColor(layer, newColor);
    }

    public static int mix(int prevColor, DyeColor... dyeColors) {
        int[] colors = new int[3];
        int i = 0;
        int j = 0;
        if (prevColor != Integer.MIN_VALUE) {
            float r = getColorAsFloat(getRedColorComponent(prevColor));
            float g = getColorAsFloat(getGreenColorComponent(prevColor));
            float b = getColorAsFloat(getBlueColorComponent(prevColor));
            i += getColorAsInt(Math.max(r, Math.max(g, b)));
            colors[0] += getColorAsInt(r);
            colors[1] += getColorAsInt(g);
            colors[2] += getColorAsInt(b);
            ++j;
        }
        for (DyeColor dyeColor : dyeColors) {
            float[] diffused = dyeColor.getTextureDiffuseColors();
            int r = getColorAsInt(diffused[0]);
            int g = getColorAsInt(diffused[1]);
            int b = getColorAsInt(diffused[2]);
            i += getColorAsInt(Math.max(r, Math.max(g, b)));
            colors[0] += r;
            colors[1] += g;
            colors[2] += b;
            ++j;
        }
        int r = colors[0] / j;
        int g = colors[1] / j;
        int b = colors[2] / j;
        float f = i / (float) j;
        float max = Math.max(r, Math.max(g, b));
        r = (int) (r * f / max);
        g = (int) (g * f / max);
        b = (int) (b * f / max);
        return (((r << 8) + g) << 8) + b;
    }

    public static int getAlphaColorComponent(int color) {
        return color >> 24 & 255;
    }

    public static int getRedColorComponent(int color) {
        return color >> 16 & 255;
    }

    public static int getGreenColorComponent(int color) {
        return color >> 8 & 255;
    }

    public static int getBlueColorComponent(int color) {
        return color & 255;
    }

    public static float getColorAsFloat(int color) {
        return color / 255.0F;
    }

    public static int getColorAsInt(float color) {
        return (int) (color * 255.0F);
    }
}
