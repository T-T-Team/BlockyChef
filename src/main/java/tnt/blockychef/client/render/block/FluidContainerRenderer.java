package tnt.blockychef.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.food.fluid.FluidContainer;
import tnt.blockychef.common.food.fluid.FluidType;
import tnt.blockychef.util.ColorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class FluidContainerRenderer {

    private static final ResourceLocation FLUID_TEXTURE = BlockyChef.resource("textures/fluid.png");
    private final List<Vertex> vertexList = new ArrayList<>();
    private float minY = 0.0f, maxY = 1.0f;

    public void setDimensions(float minY, float maxY) {
        this.minY = minY;
        this.maxY = maxY;
    }

    public void setVertices(Consumer<VertexProvider> provider) {
        vertexList.clear();
        VertexProvider prov = (ax, az, bx, bz, cx, cz, dx, dz, faces) -> vertexList.add(new Vertex(
                new Point(ax, az),
                new Point(bx, bz),
                new Point(cx, cz),
                new Point(dx, dz),
                faces
        ));
        provider.accept(prov);
    }

    public void renderFluids(FluidContainer container, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        Map<FluidType, Integer> map = container.getFluids();
        if (map.isEmpty())
            return;
        float offset = 0.0F;
        float delta = maxY - minY;
        VertexConsumer builder = bufferSource.getBuffer(RenderType.entityTranslucent(FLUID_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f m4f = pose.pose();
        Matrix3f m3f = pose.normal();
        float nx = 0;
        float ny = 1;
        float nz = 0;
        float darkenModifier = map.size() > 1 ? 1.0F : 1.0F - container.getFilledCapacityPercent(container.getAmount()) * 0.5F;
        for (Map.Entry<FluidType, Integer> entry : map.entrySet()) {
            int color = entry.getKey().fluidColor();
            int darkColor = ColorHelper.darken(color, darkenModifier);
            float f = container.getFilledCapacityPercent(entry.getValue());
            float y1 = minY + offset;
            float y2 = y1 + delta * f;
            for (Vertex vertex : vertexList) {
                // Bottom layer
                if (isVisible(Direction.DOWN, vertex)) {
                    builder.vertex(m4f, vertex.p1.x, y1, vertex.p1.z).color(darkColor).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p2.x, y1, vertex.p2.z).color(darkColor).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p3.x, y1, vertex.p3.z).color(darkColor).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p4.x, y1, vertex.p4.z).color(darkColor).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                }
                // Top layer
                if (isVisible(Direction.UP, vertex)) {
                    builder.vertex(m4f, vertex.p1.x, y2, vertex.p1.z).color(color).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p2.x, y2, vertex.p2.z).color(color).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p3.x, y2, vertex.p3.z).color(color).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p4.x, y2, vertex.p4.z).color(color).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                }
                // Side layers
                if (isVisible(Direction.NORTH, vertex)) {
                    builder.vertex(m4f, vertex.p1.x, y1, vertex.p1.z).color(darkColor).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p1.x, y2, vertex.p1.z).color(color).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p2.x, y2, vertex.p2.z).color(color).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p2.x, y1, vertex.p2.z).color(darkColor).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                }
                if (isVisible(Direction.EAST, vertex)) {
                    builder.vertex(m4f, vertex.p2.x, y1, vertex.p2.z).color(darkColor).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p2.x, y2, vertex.p2.z).color(color).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p3.x, y2, vertex.p3.z).color(color).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p3.x, y1, vertex.p3.z).color(darkColor).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                }
                if (isVisible(Direction.SOUTH, vertex)) {
                    builder.vertex(m4f, vertex.p3.x, y1, vertex.p3.z).color(darkColor).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p3.x, y2, vertex.p3.z).color(color).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p4.x, y2, vertex.p4.z).color(color).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p4.x, y1, vertex.p4.z).color(darkColor).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                }
                if (isVisible(Direction.WEST, vertex)) {
                    builder.vertex(m4f, vertex.p4.x, y1, vertex.p4.z).color(darkColor).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p4.x, y2, vertex.p4.z).color(color).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p1.x, y2, vertex.p1.z).color(color).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                    builder.vertex(m4f, vertex.p1.x, y1, vertex.p1.z).color(darkColor).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(m3f, nx, ny, nz).endVertex();
                }
            }
            offset += delta * f;
        }
    }

    private static boolean isVisible(Direction direction, Vertex vertex) {
        int ignored = vertex.invisibleFaces();
        int dirFlag = 1 << direction.ordinal();
        return (ignored & dirFlag) != dirFlag;
    }

    private record Point(float x, float z) {}

    private record Vertex(Point p1, Point p2, Point p3, Point p4, int invisibleFaces) {}

    public interface VertexProvider {

        void addShape(float ax, float az, float bx, float bz, float cx, float cz, float dx, float dz, int ignoredFaces);

        default void addShape(float ax, float az, float bx, float bz, float cx, float cz, float dx, float dz) {
            addShape(ax, az, bx, bz, cx, cz, dx, dz, 0);
        }
    }
}
