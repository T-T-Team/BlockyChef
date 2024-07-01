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
import tnt.blockychef.common.food.recipe.DryingRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;

public class DryingRecipeCategory extends BlockyChefRecipeCategory<DryingRecipe> {

    private final IDrawable progressArrow;

    public DryingRecipeCategory(IGuiHelper helper) {
        super(helper, "drying_rack");
        progressArrow = helper.drawableBuilder(backgroundPath, 72, 0, 22, 16).setTextureSize(94, 18)
                .buildAnimated(300, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    protected IDrawable createBackground(IGuiHelper helper) {
        return helper.drawableBuilder(backgroundPath, 0, 0, 72, 18).setTextureSize(94, 18).build();
    }

    @Override
    protected ItemStack getIconStack() {
        return new ItemStack(BlockyChefBlocks.DRYING_RACK);
    }

    @Override
    public void draw(DryingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        progressArrow.draw(guiGraphics, 25, 1);
        drawExperience(recipe.getExperience(), guiGraphics, 5, 10);
        drawTime(recipe.getDryingTime(), guiGraphics, 5, 0);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DryingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 55, 1).addItemStack(recipe.getOutput());
    }

    @Override
    public RecipeType<DryingRecipe> getRecipeType() {
        return JeiIntegrationPlugin.DRYING_RECIPE;
    }
}
