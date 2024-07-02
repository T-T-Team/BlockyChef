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
import tnt.blockychef.common.food.recipe.CookingTableRecipe;
import tnt.blockychef.common.food.recipe.MultiIngredient;
import tnt.blockychef.common.init.BlockyChefBlocks;

import java.util.List;

public class CookingTableRecipeCategory extends BlockyChefRecipeCategory<CookingTableRecipe> {

    private final IDrawableAnimated arrow;

    public CookingTableRecipeCategory(IGuiHelper helper) {
        super(helper, "cooking_table");
        this.arrow = helper.drawableBuilder(backgroundPath, 162, 0, 22, 16).setTextureSize(184, 54)
                .buildAnimated(300, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    protected IDrawable createBackground(IGuiHelper helper) {
        return helper.drawableBuilder(backgroundPath, 0, 0, 162, 54).setTextureSize(184, 54)
                .build();
    }

    @Override
    protected ItemStack getIconStack() {
        return new ItemStack(BlockyChefBlocks.PP_COOKING_TABLE);
    }

    @Override
    public void draw(CookingTableRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 77, 19);
        drawCentered(getTimeLabel(recipe.getAssemblyTime()).getString(), guiGraphics, 0, 3, 0x808080, false);
        float exp = recipe.getExperience();
        if (exp > 0) {
            drawCentered(getExperienceLabel(exp).getString(), guiGraphics, 0, 25, 0x808080, false);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CookingTableRecipe recipe, IFocusGroup focuses) {
        List<List<ItemStack>> inputs = recipe.getInputs().stream()
                .map(MultiIngredient::toItemStackList).toList();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int index = x + y * 3;
                if (index >= inputs.size())
                    break;
                builder.addSlot(RecipeIngredientRole.INPUT, 1 + x * 18, 1 + y * 18)
                        .addItemStacks(inputs.get(index));
            }
        }

        List<ItemStack> outputs = recipe.getOutputs();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 2; x++) {
                int index = x + y * 2;
                if (index >= outputs.size())
                    break;
                builder.addSlot(RecipeIngredientRole.OUTPUT, 127 + x * 18, 1 + y * 18)
                        .addItemStack(outputs.get(index));
            }
        }
    }

    @Override
    public RecipeType<CookingTableRecipe> getRecipeType() {
        return JeiIntegrationPlugin.COOKING_TABLE;
    }
}
