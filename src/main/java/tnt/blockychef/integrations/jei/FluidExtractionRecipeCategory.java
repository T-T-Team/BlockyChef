package tnt.blockychef.integrations.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tnt.blockychef.common.data.fluids.FluidExtraction;
import tnt.blockychef.common.init.BlockyChefItems;

public class FluidExtractionRecipeCategory extends BlockychefFluidRecipeCategory<FluidExtraction> {

    private final IDrawableAnimated progress;

    public FluidExtractionRecipeCategory(IGuiHelper helper) {
        super(helper, "fluid_extraction");
        this.progress = helper.drawableBuilder(backgroundPath, 93, 0, 18, 19).setTextureSize(111, 54)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    protected IDrawable createBackground(IGuiHelper helper) {
        return helper.drawableBuilder(backgroundPath, 0, 0, 93, 54).setTextureSize(111, 54)
                .build();
    }

    @Override
    protected ItemStack getIconStack() {
        return new ItemStack(BlockyChefItems.EMPTY_LARGE_GLASS);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidExtraction recipe, IFocusGroup focuses) {
        FluidStack fluidStack = recipe.getFluid();
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 6).addFluidStack(fluidStack.getFluid(), 1000L);
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 32).addItemStack(recipe.getInputItem());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 19).addItemStack(recipe.getOutputItem());
    }

    @Override
    public void draw(FluidExtraction recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        fluidIconsOverlay.draw(guiGraphics, 1, 6);
        progress.draw(guiGraphics, 38, 18);
        drawCentered(getValueLabel(recipe.getFluid().getAmount()).getString(), guiGraphics, 0, 40, 0x808080, false);
    }

    @Override
    public RecipeType<FluidExtraction> getRecipeType() {
        return JeiIntegrationPlugin.FLUID_EXTRACTION;
    }
}
