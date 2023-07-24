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
import tnt.blockychef.common.block.entity.CuttingBoardBlockEntity;
import tnt.blockychef.common.food.recipe.PastaMachineRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;

public class PastaMachineRecipeCategory extends BlockyChefRecipeCategory<PastaMachineRecipe> {

    private final IDrawable progressArrow;

    public PastaMachineRecipeCategory(IGuiHelper helper) {
        super(helper, "pasta_machine");
        progressArrow = helper.drawableBuilder(backgroundPath, 126, 0, 26, 12).setTextureSize(152, 54)
                .buildAnimated(300, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    protected IDrawable createBackground(IGuiHelper helper) {
        return helper.drawableBuilder(backgroundPath, 0, 0, 126, 54).setTextureSize(152, 54).build();
    }

    @Override
    protected ItemStack getIconStack() {
        return new ItemStack(BlockyChefBlocks.PASTA_MACHINE);
    }

    @Override
    public void draw(PastaMachineRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        progressArrow.draw(guiGraphics, 50, 21);
        drawExperience(recipe.getExperience(), guiGraphics, 5, 10);
        drawTime(recipe.getProcessingTime(), guiGraphics, 5, 0);

        Component title = recipe.getProcessingType().getTranslatedComponent();
        Font font = Minecraft.getInstance().font;
        int width = font.width(title);
        guiGraphics.drawString(font, title.getString(), (background.getWidth() - width) / 2.0F, 5, 0x808080, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PastaMachineRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 19).addIngredients(recipe.getInput());
        ItemStack[] outputs = recipe.getOutputs();
        for (int i = 0; i < Math.min(outputs.length, CuttingBoardBlockEntity.SLOT_OUTPUTS.length); i++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 1 + i * 18).addItemStack(outputs[i]);
        }
    }

    @Override
    public RecipeType<PastaMachineRecipe> getRecipeType() {
        return JeiIntegrationPlugin.PASTA_MACHINE;
    }
}
