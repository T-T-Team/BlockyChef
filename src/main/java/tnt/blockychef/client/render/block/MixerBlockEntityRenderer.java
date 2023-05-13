package tnt.blockychef.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import tnt.blockychef.common.block.entity.MixerBlockEntity;

public class MixerBlockEntityRenderer implements BlockEntityRenderer<MixerBlockEntity> {

    private final ItemRenderer itemRenderer;
    private final FluidContainerRenderer fluidRenderer;

    public MixerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.fluidRenderer = new FluidContainerRenderer();
    }

    @Override
    public void render(MixerBlockEntity mixer, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        float p1 = 0.35F;
        float p2 = 1.0F - p1;
        fluidRenderer.setDimensions(0.38F, 0.83F);
        fluidRenderer.setVertices(builder -> builder.addShape(p1, p1, p1, p2, p2, p2, p2, p1));
        fluidRenderer.renderFluids(mixer.getFluids(), poseStack, bufferSource, light);
    }
}
