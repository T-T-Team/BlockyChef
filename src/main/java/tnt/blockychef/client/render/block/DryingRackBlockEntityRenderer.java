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
import tnt.blockychef.common.block.DryingRackBlock;
import tnt.blockychef.common.block.entity.DryingRackBlockEntity;

public class DryingRackBlockEntityRenderer implements BlockEntityRenderer<DryingRackBlockEntity> {

    private final ItemRenderer itemRenderer;

    public DryingRackBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(DryingRackBlockEntity dryingRack, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        ItemStack stack = dryingRack.getItem(0);
        if (stack.isEmpty())
            return;

        Direction direction = dryingRack.getBlockState().getValue(DryingRackBlock.FACING);
        float itemRenderScale = 0.4F;
        float yRotation = direction.get2DDataValue() * 90.0F;
        poseStack.pushPose();
        poseStack.translate(0.0, 0.8, 0.0);
        // Could be propably done in a better way
        switch (direction) {
            case NORTH -> poseStack.translate(0.5, 0.0, 0.85);
            case SOUTH -> poseStack.translate(0.5, 0.0, 0.15);
            case EAST -> poseStack.translate(0.15, 0.0, 0.5);
            case WEST -> poseStack.translate(0.85, 0.0, 0.5);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yRotation));
        poseStack.scale(itemRenderScale, itemRenderScale, itemRenderScale);
        itemRenderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, 0);
        poseStack.popPose();
    }
}
