package tnt.blockychef.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import tnt.blockychef.common.block.DryingRackBlock;
import tnt.blockychef.common.block.entity.DryingRackBlockEntity;

public class DryingRackBlockEntityRenderer extends BlockyChefBlockEntityRenderer<DryingRackBlockEntity> {

    public DryingRackBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(DryingRackBlockEntity dryingRack, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        Direction direction = dryingRack.getBlockState().getValue(DryingRackBlock.FACING);
        float itemScale = 0.4F;
        for (int i = 0; i < DryingRackBlockEntity.DRYING_CAPACITY; i++) {
            ItemStack stack = dryingRack.getItem(i);
            if (stack.isEmpty())
                continue;
            poseStack.pushPose();
            Direction.Axis axis = direction.getAxis();
            Direction.AxisDirection axisDirection = direction.getAxisDirection();
            int j = (axis == Direction.Axis.X && axisDirection == Direction.AxisDirection.NEGATIVE) || (axis == Direction.Axis.Z && axisDirection == Direction.AxisDirection.POSITIVE) ? i : DryingRackBlockEntity.DRYING_CAPACITY - i - 1;
            double position = 0.2 + j * 0.3;
            double height = 0.8;
            double wallOffset = 0.15;
            switch (direction) {
                case NORTH -> {
                    poseStack.translate(position, height, 1.0 - wallOffset);
                }
                case SOUTH -> {
                    poseStack.translate(position, height, wallOffset);
                    poseStack.mulPose(Axis.YP.rotation((float) Math.PI));
                }
                case EAST -> {
                    poseStack.translate(wallOffset, height, position);
                    poseStack.mulPose(Axis.YP.rotation(HALF_PI * 3));
                }
                case WEST -> {
                    poseStack.translate(1.0 - wallOffset, height, position);
                    poseStack.mulPose(Axis.YP.rotation(HALF_PI));
                }
            }

            poseStack.scale(itemScale, itemScale, itemScale);
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, dryingRack.getLevel(), 0);
            poseStack.popPose();
        }
    }
}
