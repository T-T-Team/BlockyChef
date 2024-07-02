package tnt.blockychef.integrations.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import tnt.blockychef.common.food.recipe.GrillRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.tntlib.api.ColorPalette;

public class GrillRecipeCategory extends BlockyChefRecipeCategory<GrillRecipe> {

    private final IDrawableAnimated flame;
    private final IDrawableAnimated arrow;

    public GrillRecipeCategory(IGuiHelper helper) {
        super(helper, "grill");
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
        return new ItemStack(BlockyChefBlocks.GRILL);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GrillRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 73, 1).addItemStack(recipe.getResult());
    }

    @Override
    public void draw(GrillRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        flame.draw(guiGraphics, 18, 20);
        arrow.draw(guiGraphics, 33, 1);
        drawTime(recipe.getConfiguration().time(), guiGraphics, 5, 30);
        drawExperience(recipe.getExperience(), guiGraphics, 5, 0);

        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, recipe.getConfiguration().getTemperatureRange(), 35, 25, ColorPalette.GUI_TEXT_LIGHT_GRAY, false);
    }

    @Override
    public RecipeType<GrillRecipe> getRecipeType() {
        return JeiIntegrationPlugin.GRILL;
    }
}
