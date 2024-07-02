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
import tnt.blockychef.common.food.recipe.MeatGrinderRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;

public class MeatGrinderRecipeCategory extends BlockyChefRecipeCategory<MeatGrinderRecipe> {

    private final IDrawableAnimated progressArrow;

    public MeatGrinderRecipeCategory(IGuiHelper helper) {
        super(helper, "meat_grinder");
        progressArrow = helper.drawableBuilder(backgroundPath, 72, 0, 22, 16).setTextureSize(94, 18)
                .buildAnimated(300, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    protected IDrawable createBackground(IGuiHelper helper) {
        return helper.drawableBuilder(backgroundPath, 0, 0, 72, 18).setTextureSize(94, 18).build();
    }

    @Override
    protected ItemStack getIconStack() {
        return new ItemStack(BlockyChefBlocks.MEAT_GRINDER);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MeatGrinderRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 55, 1).addItemStack(recipe.getOutput());
    }

    @Override
    public void draw(MeatGrinderRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        progressArrow.draw(guiGraphics, 25, 1);
        drawExperience(recipe.getExperience(), guiGraphics, 5, 10);
        String text = recipe.getProcessingAmount() + "x";
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, text, background.getWidth() + 5, (background.getHeight() - font.lineHeight) / 2.0F, 0x808080, false);
    }

    @Override
    public RecipeType<MeatGrinderRecipe> getRecipeType() {
        return JeiIntegrationPlugin.MEAT_GRINDER;
    }
}
