package tnt.tntlib.api;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;

public final class GraphicsHelper {

    public static void drawAlignedText(GuiGraphics graphics, Component text, Font font, HorizontalAlignment horizontal, VerticalAlignment vertical, float left, float top, float width, float height, int color, boolean shadow, float xOffset, float yOffset) {
        int textWidth = font.width(text);
        float x = horizontal.align(left, width, textWidth) + xOffset;
        float y = vertical.align(top, height, font.lineHeight) + yOffset;
        graphics.drawString(font, text.getVisualOrderText(), x, y, color, shadow);
    }

    public static void drawAlignedText(GuiGraphics graphics, Component text, Font font, HorizontalAlignment horizontal, VerticalAlignment vertical, float left, float top, float width, float height, int color, boolean shadow) {
        drawAlignedText(graphics, text, font, horizontal, vertical, left, top, width, height, color, shadow, 0, 0);
    }

    public static void drawAlignedText(GuiGraphics graphics, Component text, Font font, HorizontalAlignment horizontal, VerticalAlignment vertical, float left, float top, float width, float height, int color) {
        drawAlignedText(graphics, text, font, horizontal, vertical, left, top, width, height, color, false);
    }

    public static void drawCenteredText(GuiGraphics graphics, Component text, Font font, float left, float top, float width, float height, int color, boolean shadow) {
        drawAlignedText(graphics, text, font, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, left, top, width, height, color, shadow);
    }

    public static void drawCenteredText(GuiGraphics graphics, Component text, Font font, float left, float top, float width, float height, int color) {
        drawCenteredText(graphics, text, font, left, top, width, height, color, false);
    }

    public static void drawSelfCenteredText(GuiGraphics graphics, Component text, Font font, float x, float y, int color, boolean shadow, float xOffset, float yOffset) {
        graphics.drawString(font, text.getVisualOrderText(), x + font.width(text) / 2.0F + xOffset, y + font.lineHeight / 2.0F + yOffset, color, shadow);
    }

    public static void drawSelfCenteredText(GuiGraphics graphics, Component text, Font font, float x, float y, int color, boolean shadow) {
        drawSelfCenteredText(graphics, text, font, x, y, color, shadow, 0.0F, 0.0F);
    }

    public static void drawSelfCenteredText(GuiGraphics graphics, Component text, Font font, float x, float y, int color) {
        drawSelfCenteredText(graphics, text, font, x, y, color, false);
    }

    public static void drawRightAlignedText(GuiGraphics graphics, Component text, Font font, float x, float y, int color, boolean shadow) {
        int width = font.width(text);
        graphics.drawString(font, text.getVisualOrderText(), x - width, y, color, shadow);
    }

    public static void drawRightAlignedText(GuiGraphics graphics, Component text, Font font, float x, float y, int color) {
        drawRightAlignedText(graphics, text, font, x, y, color, false);
    }

    public static int setAlpha(float alpha, int originalARGB) {
        int red = FastColor.ARGB32.red(originalARGB);
        int green = FastColor.ARGB32.green(originalARGB);
        int blue = FastColor.ARGB32.blue(originalARGB);
        return FastColor.ARGB32.color((int) (alpha * 255F), red, green, blue);
    }

    public static int fullAlpha(int originalARGB) {
        return setAlpha(1.0F, originalARGB);
    }

    private GraphicsHelper() {}
}
