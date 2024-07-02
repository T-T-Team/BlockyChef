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
import tnt.blockychef.common.food.recipe.ToasterRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;

public class ToastingRecipeCategory extends BlockyChefRecipeCategory<ToasterRecipe> {

    private final IDrawableAnimated flame;
    private final IDrawableAnimated arrow;

    public ToastingRecipeCategory(IGuiHelper helper) {
        super(helper, "toasting");
        this.flame = helper.drawableBuilder(backgroundPath, 90, 16, 14, 14).setTextureSize(112, 38)
                .buildAnimated(200, IDrawableAnimated.StartDirection.BOTTOM, false);
        this.arrow = helper.drawableBuilder(backgroundPath, 90, 0, 22, 16).setTextureSize(112, 38)
                .buildAnimated(300, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    protected IDrawable createBackground(IGuiHelper helper) {
        return helper.drawableBuilder(backgroundPath, 0, 0, 90, 38).setTextureSize(112, 38).build();
    }

    @Override
    protected ItemStack getIconStack() {
        return new ItemStack(BlockyChefBlocks.TOASTER);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ToasterRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 73, 1).addItemStack(recipe.getOutput());
    }

    @Override
    public void draw(ToasterRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        flame.draw(guiGraphics, 18, 20);
        arrow.draw(guiGraphics, 33, 1);
        drawTime(recipe.getToastingTime(), guiGraphics, -55, 25);
        drawExperience(recipe.getExperience(), guiGraphics, 5, 0);
    }

    @Override
    public RecipeType<ToasterRecipe> getRecipeType() {
        return JeiIntegrationPlugin.TOASTER_RECIPE;
    }
}
