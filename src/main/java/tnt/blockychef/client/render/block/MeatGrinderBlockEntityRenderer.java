package tnt.blockychef.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import tnt.blockychef.common.block.MeatGrinderBlock;
import tnt.blockychef.common.block.entity.MeatGrinderBlockEntity;
import tnt.blockychef.common.food.recipe.MeatGrinderRecipe;

public class MeatGrinderBlockEntityRenderer extends BlockyChefBlockEntityRenderer<MeatGrinderBlockEntity> {

    public MeatGrinderBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(MeatGrinderBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSrc, int light, int overlay) {
        ItemStack itemStack = blockEntity.getInputItem();
        if (itemStack.isEmpty())
            return;
        float scale = 0.2F;
        MeatGrinderRecipe recipe = blockEntity.getRecipe();
        int amount = recipe != null ? blockEntity.getGrindAmount() : 0;
        int total = recipe != null ? recipe.getProcessingAmount() : 1;
        float progress = amount / (float) total;

        Direction direction = blockEntity.getBlockState().getValue(MeatGrinderBlock.FACING);
        poseStack.pushPose();

        translateWithRotation(poseStack, direction, 0.40, 0.6 - progress * 0.2F, 0.5);
        poseStack.mulPose(Axis.YP.rotation(HALF_PI));
        poseStack.mulPose(Axis.YP.rotationDegrees(10.0F * amount));
        poseStack.mulPose(Axis.YP.rotation(direction.get2DDataValue() * HALF_PI));

        poseStack.scale(scale, scale, scale);
        itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, bufferSrc, blockEntity.getLevel(), 0);
        poseStack.popPose();
    }
}
