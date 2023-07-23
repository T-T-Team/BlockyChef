package tnt.blockychef.integrations.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.food.recipe.DryingRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;

public class DryingRecipeCategory implements IRecipeCategory<DryingRecipe> {

    private static final Component TITLE = Component.translatable("jei.recipe.blockychef.drying");
    private static final ResourceLocation BACKGROUND_PATH = BlockyChef.resource("textures/screen/jei/drying_rack.png");
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable progressArrow;

    public DryingRecipeCategory(IGuiHelper helper) {
        background = helper.drawableBuilder(BACKGROUND_PATH, 0, 0, 72, 18).setTextureSize(94, 18).build();
        progressArrow = helper.drawableBuilder(BACKGROUND_PATH, 72, 0, 22, 16).setTextureSize(94, 18)
                .buildAnimated(300, IDrawableAnimated.StartDirection.LEFT, false);
        icon = helper.createDrawableItemStack(new ItemStack(BlockyChefBlocks.DRYING_RACK));
    }

    @Override
    public void draw(DryingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        progressArrow.draw(guiGraphics, 25, 1);
        drawExperience(recipe, guiGraphics, 5, 10);
        drawDryTime(recipe, guiGraphics, 5, 0);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DryingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 55, 1).addItemStack(recipe.getOutput());
    }

    private void drawExperience(DryingRecipe recipe, GuiGraphics guiGraphics, int x, int y) {
        float experience = recipe.getExperience();
        if (experience > 0) {
            Component experienceString = Component.translatable("gui.jei.category.smelting.experience", experience);
            Font font = Minecraft.getInstance().font;
            guiGraphics.drawString(font, experienceString, x + background.getWidth(), y, 0xFF808080, false);
        }
    }

    private void drawDryTime(DryingRecipe recipe, GuiGraphics guiGraphics, int x, int y) {
        int dryingTime = recipe.getDryingTime();
        if (dryingTime > 0) {
            int dryingSeconds = dryingTime / 20;
            Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", dryingSeconds);
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            guiGraphics.drawString(fontRenderer, timeString, x + background.getWidth(), y, 0xFF808080, false);
        }
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public RecipeType<DryingRecipe> getRecipeType() {
        return JeiPlugin.DRYING_RECIPE;
    }
}
