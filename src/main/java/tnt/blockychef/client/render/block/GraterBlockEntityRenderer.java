package tnt.blockychef.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import tnt.blockychef.common.block.GraterBlock;
import tnt.blockychef.common.block.entity.GraterBlockEntity;

public class GraterBlockEntityRenderer implements BlockEntityRenderer<GraterBlockEntity> {

    private final ItemRenderer renderer;

    public GraterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.renderer = context.getItemRenderer();
    }

    @Override
    public void render(GraterBlockEntity grater, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        ItemStack stack = grater.getItem(0);
        if (stack.isEmpty()) {
            return;
        }
        float renderScale = 0.4F;
        float gratingProgress = grater.getGratingProgress();
        float gratingAmount = 0.05F * gratingProgress;
        Direction direction = grater.getBlockState().getValue(GraterBlock.FACING);
        poseStack.pushPose();
        poseStack.translate(0.5, 0.25, 0.5);
        poseStack.scale(renderScale, renderScale, renderScale);
        switch (direction) {
            case NORTH -> {
                poseStack.translate(0.0, 0.0, -0.27 + gratingAmount);
                poseStack.mulPose(Axis.XP.rotationDegrees(22.5F));
            }
            case SOUTH -> {
                poseStack.translate(0.0, 0.0, 0.27 - gratingAmount);
                poseStack.mulPose(Axis.XN.rotationDegrees(22.5F));
            }
            case EAST -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.XN.rotationDegrees(22.5F));
                poseStack.translate(0.0, 0.0, 0.26 - gratingAmount);
            }
            case WEST -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(22.5F));
                poseStack.translate(0.0, 0.0, -0.26 + gratingAmount);
            }
        }
        renderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, 0);
        poseStack.popPose();
    }
}
