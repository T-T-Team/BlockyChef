package tnt.blockychef.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BlockyChefBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

    protected static final float HALF_PI = (float) (Math.PI / 2.0F);
    protected final ItemRenderer itemRenderer;

    public BlockyChefBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    protected static void translateWithRotation(PoseStack poseStack, Direction direction, double x, double y, double z) {
        double mx = x;
        double mz = z;
        switch (direction) {
            case SOUTH -> {
                mx = 1.0 - x;
            }
            case EAST -> {
                mx = z;
                mz = x;
            }
            case WEST -> {
                mx = z;
                mz = 1.0 - x;
            }
        }
        poseStack.translate(mx, y, mz);
    }
}
