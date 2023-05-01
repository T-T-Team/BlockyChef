package tnt.blockychef.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.function.UnaryOperator;

public final class RenderHelper {

    public static void texturedBlit(Matrix4f pose, float x1, float y1, float x2, float y2, float z, float u1, float v1, float u2, float v2) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(pose, x1, y1, z).uv(u1, v1).endVertex();
        buffer.vertex(pose, x1, y2, z).uv(u1, v2).endVertex();
        buffer.vertex(pose, x2, y2, z).uv(u2, v2).endVertex();
        buffer.vertex(pose, x2, y1, z).uv(u2, v1).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    public static float interpolate(float previous, float current, float partialTicks) {
        return previous + (current - previous) * partialTicks;
    }

    public static float ease(float in, Easing easing) {
        return easing.process(in);
    }

    public enum Easing {

        SINE_IO(Easing::inOutSine);

        private static final float PI = (float) Math.PI;
        private final UnaryOperator<Float> processor;

        Easing(UnaryOperator<Float> processor) {
            this.processor = processor;
        }

        public float process(float in) {
            return processor.apply(in);
        }

        private static float inOutSine(float f) {
            return -(Mth.cos(PI * f) - 1.0F) / 2.0F;
        }
    }
}
