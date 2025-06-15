package tnt.blockychef.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import tnt.blockychef.common.block.GraterBlock;
import tnt.blockychef.common.block.entity.ToasterBlockEntity;

public class ToasterBlockEntityRenderer implements BlockEntityRenderer<ToasterBlockEntity> {

    private final ItemRenderer renderer;

    public ToasterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.renderer = context.getItemRenderer();
    }

    @Override
    public void render(ToasterBlockEntity toasterBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int i1) {
        ItemStack item1 = toasterBlockEntity.getItem(0);
        ItemStack item2 = toasterBlockEntity.getItem(1);
        float scale = 0.35f;
        float y = toasterBlockEntity.isToasting() ? 0.3f : 0.5f;

        poseStack.pushPose();
        poseStack.translate(0.5, y, 0.5);
        poseStack.scale(scale, scale, scale);
        Direction direction = toasterBlockEntity.getBlockState().getValue(GraterBlock.FACING);

        if (direction.getAxis() == Direction.Axis.X) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90F));
        }

        if (!item1.isEmpty()) {
            poseStack.pushPose();
            switch (direction) {
                case NORTH, WEST -> {
                    poseStack.translate(0.08, 0.0, -0.23);
                }
                case SOUTH, EAST -> {
                    poseStack.translate(-0.08, 0.0, 0.23);
                }
            }
            renderer.renderStatic(item1, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, toasterBlockEntity.getLevel(), 0);
            poseStack.popPose();
        }
        if (!item2.isEmpty()) {
            poseStack.pushPose();
            switch (direction) {
                case NORTH, WEST -> {
                    poseStack.translate(0.08, 0.0, 0.23);
                }
                case SOUTH, EAST -> {
                    poseStack.translate(-0.08, 0.0, -0.23);
                }
            }
            renderer.renderStatic(item2, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, toasterBlockEntity.getLevel(), 0);
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
