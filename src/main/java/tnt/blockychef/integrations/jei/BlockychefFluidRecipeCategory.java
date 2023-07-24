package tnt.blockychef.integrations.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.food.fluid.Fluid;
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

    public void drawFluid(GuiGraphics guiGraphics, Fluid fluid, int x, int y, double mouseX, double mouseY) {
        int color = fluid.getFluidType().fluidColor();
        fluidIcon.setTint(color);
        fluidIcon.draw(guiGraphics, x, y);
        fluidIconsOverlay.draw(guiGraphics, x, y);
        boolean isHovered = mouseX >= x && mouseX <= x + getFluidWidth() && mouseY >= y && mouseY <= y + getFluidHeight();
        if (isHovered) {
            guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, fluid.getTooltip(), (int) mouseX, (int) mouseY);
        }
    }

    protected int getFluidWidth() {
        return 16;
    }

    protected int getFluidHeight() {
        return 16;
    }
}
