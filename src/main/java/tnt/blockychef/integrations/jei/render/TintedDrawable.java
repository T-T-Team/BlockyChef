package tnt.blockychef.integrations.jei.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class TintedDrawable implements IDrawable {

    private final ResourceLocation resourceLocation;
    private final int u;
    private final int v;
    private final int width;
    private int height;
    private int textureWidth = 256;
    private int textureHeight = 256;
    private int tint = 0xFFFFFF;

    public TintedDrawable(ResourceLocation resourceLocation, int u, int v, int width, int height) {
        this.resourceLocation = resourceLocation;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
    }

    public void setTextureSize(int width, int height) {
        this.textureWidth = width;
        this.textureHeight = height;
    }

    public void setTint(int tint) {
        this.tint = tint;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        float u1 = u / (float) textureWidth;
        float v1 = v / (float) textureHeight;
        float u2 = u1 + width / (float) textureWidth;
        float v2 = v1 + height / (float) textureHeight;
        colorBlit(guiGraphics.pose(), resourceLocation, xOffset, xOffset + width, yOffset, yOffset + height, u1, v1, u2, v2, tint);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    void colorBlit(PoseStack stack, ResourceLocation pAtlasLocation, int pX1, int pX2, int pY1, int pY2, float pMinU, float pMaxU, float pMinV, float pMaxV, int color) {
        RenderSystem.setShaderTexture(0, pAtlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        Matrix4f matrix4f = stack.last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        bufferbuilder.vertex(matrix4f, (float)pX1, (float)pY1, 0).color(color).uv(pMinU, pMinV).endVertex();
        bufferbuilder.vertex(matrix4f, (float)pX1, (float)pY2, 0).color(color).uv(pMinU, pMaxV).endVertex();
        bufferbuilder.vertex(matrix4f, (float)pX2, (float)pY2, 0).color(color).uv(pMaxU, pMaxV).endVertex();
        bufferbuilder.vertex(matrix4f, (float)pX2, (float)pY1, 0).color(color).uv(pMaxU, pMinV).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }
}
