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
import tnt.blockychef.common.food.recipe.MultiIngredient;
import tnt.blockychef.common.food.recipe.SaucepanRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.tntlib.api.ColorPalette;
import tnt.tntlib.api.GraphicsHelper;

import java.util.List;

public class SaucepanRecipeCategory extends BlockyChefRecipeCategory<SaucepanRecipe> {

    private final IDrawableAnimated progressArrow;
    private final IDrawableAnimated flame;

    public SaucepanRecipeCategory(IGuiHelper helper) {
        super(helper, "saucepan");
        this.progressArrow = helper.drawableBuilder(backgroundPath, 162, 0, 22, 16).setTextureSize(184, 55)
                .buildAnimated(300, IDrawableAnimated.StartDirection.LEFT, false);
        this.flame = helper.drawableBuilder(backgroundPath, 162, 16, 14, 14).setTextureSize(184, 55)
                .buildAnimated(350, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Override
    protected IDrawable createBackground(IGuiHelper helper) {
        return helper.drawableBuilder(backgroundPath, 0, 0, 162, 55).setTextureSize(184, 55).build();
    }

    @Override
    protected ItemStack getIconStack() {
        return new ItemStack(BlockyChefBlocks.SAUCEPAN);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SaucepanRecipe recipe, IFocusGroup focuses) {
        List<List<ItemStack>> inputs = recipe.getInputs().stream()
                .map(MultiIngredient::toItemStackList).toList();
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                int index = x + y * 3;
                if (index >= inputs.size())
                    break;
                builder.addSlot(RecipeIngredientRole.INPUT, 1 + x * 18, 1 + y * 18).addItemStacks(inputs.get(index));
            }
        }
        List<ItemStack> outputs = recipe.getOutputs();
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                int index = x + y * 2;
                if (index >= outputs.size())
                    break;
                builder.addSlot(RecipeIngredientRole.OUTPUT, 127 + x * 18, 1 + y * 18).addItemStack(outputs.get(index));
            }
        }
    }

    @Override
    public void draw(SaucepanRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        progressArrow.draw(guiGraphics, 79, 10);
        flame.draw(guiGraphics, 53, 39);

        Font font = Minecraft.getInstance().font;
        SaucepanRecipe.SaucePanCookingConfiguration cfg = recipe.getConfiguration();
        int timeToCook = cfg.time();
        GraphicsHelper.drawSelfCenteredText(guiGraphics, getTimeLabel(timeToCook), font, 73, 24, ColorPalette.GUI_TEXT_LIGHT_GRAY);
        GraphicsHelper.drawRightAlignedText(guiGraphics, cfg.getTemperatureRange(), font, 52, 44, ColorPalette.GUI_TEXT_LIGHT_GRAY);

        float experience = recipe.getExperience();
        if (experience > 0) {
            GraphicsHelper.drawRightAlignedText(guiGraphics, getExperienceLabel(experience), font, 160, 44, ColorPalette.GUI_TEXT_LIGHT_GRAY);
        }
    }

    @Override
    public RecipeType<SaucepanRecipe> getRecipeType() {
        return JeiIntegrationPlugin.SAUCEPAN;
    }
}
