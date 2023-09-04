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
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import tnt.blockychef.common.food.recipe.StoveRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.tntlib.api.GraphicsHelper;
import tnt.tntlib.api.HorizontalAlignment;
import tnt.tntlib.api.VerticalAlignment;

public class StoveRecipeCategory extends BlockyChefRecipeCategory<StoveRecipe> {

    private final IDrawableAnimated flame;
    private final IDrawableAnimated arrow;

    public StoveRecipeCategory(IGuiHelper helper) {
        super(helper, "stove");
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
        return new ItemStack(BlockyChefBlocks.STOVE);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, StoveRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 73, 1).addItemStack(recipe.getResult());
    }

    @Override
    public void draw(StoveRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        flame.draw(guiGraphics, 18, 20);
        arrow.draw(guiGraphics, 33, 1);
        Font font = Minecraft.getInstance().font;
        GraphicsHelper.drawAlignedText(guiGraphics, getTimeLabel(recipe.getConfiguration().time()), font, HorizontalAlignment.LEFT, VerticalAlignment.TOP, 0, 0, 90, 38, 0xFF808080, false, 0, 27);
        GraphicsHelper.drawAlignedText(guiGraphics, recipe.getConfiguration().getTemperatureRange(), font, HorizontalAlignment.RIGHT, VerticalAlignment.TOP, 0, 0, 90, 38, 0xFF808080, false, 0, 27);
    }

    @Override
    public RecipeType<StoveRecipe> getRecipeType() {
        return JeiIntegrationPlugin.STOVE;
    }
}
