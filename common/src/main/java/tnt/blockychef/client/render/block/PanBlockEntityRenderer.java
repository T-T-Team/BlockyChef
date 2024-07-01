package tnt.blockychef.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector2i;
import tnt.blockychef.common.block.entity.PanBlockEntity;

public class PanBlockEntityRenderer implements BlockEntityRenderer<PanBlockEntity> {

    private static final Vec2[] ITEM_POSITIONS = {
            new Vec2(0.5F, 0.35F),
            new Vec2(0.6F, 0.45F),
            new Vec2(0.56F, 0.58F),
            new Vec2(0.44F, 0.58F),
            new Vec2(0.4F, 0.45F)
    };
    private final ItemRenderer renderer;

    public PanBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.renderer = context.getItemRenderer();
    }

    @Override
    public void render(PanBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        float itemScale = 0.15F;
        float itemY = 0.05F;
        Level level = pBlockEntity.getLevel();
        for (int slot : PanBlockEntity.INPUTS) {
            ItemStack itemStack = pBlockEntity.getItem(slot);
            if (itemStack.isEmpty())
                continue;
            pPoseStack.pushPose();
            Vec2 pos = ITEM_POSITIONS[slot];
            pPoseStack.translate(pos.x, itemY, pos.y);
            pPoseStack.scale(itemScale, itemScale, itemScale);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90F));
            renderer.renderStatic(itemStack, ItemDisplayContext.FIXED, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, level, 0);
            pPoseStack.popPose();
        }
    }
}
