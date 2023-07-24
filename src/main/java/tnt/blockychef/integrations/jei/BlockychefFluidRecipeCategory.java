package tnt.blockychef.integrations.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidType;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.integrations.jei.render.TintedDrawable;

public abstract class BlockychefFluidRecipeCategory<T> extends BlockyChefRecipeCategory<T> {

    protected final TintedDrawable fluidIcon;
    protected final IDrawable fluidIconsOverlay;

    public BlockychefFluidRecipeCategory(IGuiHelper helper, String name) {
        super(helper, name);
        this.fluidIcon = new TintedDrawable(BlockyChef.resource("textures/screen/jei/fluid.png"), 0, 0, getFluidWidth(), getFluidHeight());
        this.fluidIcon.setTextureSize(16, 16);
        this.fluidIconsOverlay = helper.drawableBuilder(BlockyChef.resource("textures/screen/jei/fluids_foreground.png"), 0, 0, getFluidWidth(), getFluidHeight())
                .setTextureSize(getFluidWidth(), getFluidHeight()).build();
    }

    public Component getValueLabel(int amount) {
        return Component.translatable("label.blockychef.fluid_amount", amount);
    }

    protected int getFluidWidth() {
        return 16;
    }

    protected int getFluidHeight() {
        return 16;
    }

    protected static int adjustToCapacity(int value, int capacity) {
        float f = value / (float) capacity;
        return (int) (f * FluidType.BUCKET_VOLUME);
    }
}
