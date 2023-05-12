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
import tnt.blockychef.common.block.JuicerBlock;
import tnt.blockychef.common.block.entity.JuicerBlockEntity;
import tnt.blockychef.util.Helper;

public class JuicerBlockEntityRenderer implements BlockEntityRenderer<JuicerBlockEntity> {

    private final ItemRenderer itemRenderer;
    private final FluidContainerRenderer fluidRenderer;

    public JuicerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.fluidRenderer = new FluidContainerRenderer();
    }

    @Override
    public void render(JuicerBlockEntity juicer, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        renderItem(juicer, poseStack, bufferSource, light);
        Direction direction = juicer.getBlockState().getValue(JuicerBlock.FACING);
        fluidRenderer.setDimensions(0.02F, 0.18F);
        fluidRenderer.setVertices(builder -> {
            switch (direction) {
                case NORTH -> {
                    builder.addShape(0.21F, 0.33F, 0.555F, 0.33F, 0.555F, 0.67F, 0.21F, 0.67F, Helper.getEnumFlags(Direction.EAST));
                    builder.addShape(0.555F, 0.33F, 0.555F, 0.67F, 0.83F, 0.55F, 0.83F, 0.45F, Helper.getEnumFlags(Direction.NORTH));
                }
                case SOUTH -> {
                    // TODO south direction
                }
                case WEST -> {
                    // TODO west direction
                }
                case EAST -> {
                    // TODO east direction
                }
            }
        });
        fluidRenderer.renderFluids(juicer.getFluids(), poseStack, bufferSource, light);
    }

    private void renderItem(JuicerBlockEntity juicer, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        ItemStack stack = juicer.getInputItem();
        if (stack.isEmpty())
            return;
        float scaleF = 0.25F;
        Direction direction = juicer.getBlockState().getValue(JuicerBlock.FACING);
        poseStack.pushPose();
        poseStack.translate(0.5, 0.32, 0.5);
        switch (direction) {
            case NORTH -> poseStack.translate(-0.12, 0.0, 0.0);
            case SOUTH -> poseStack.translate(0.12, 0.0, 0.0);
            case WEST -> poseStack.translate(0.0, 0.0, 0.12);
            case EAST -> poseStack.translate(0.0, 0.0, -0.12);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(30.0F * juicer.getPressAmount()));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.scale(scaleF, scaleF, scaleF);
        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, juicer.getLevel(), 0);
        poseStack.popPose();
    }
}
