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
import tnt.blockychef.common.food.recipe.DoughMakerRecipe;
import tnt.blockychef.common.food.recipe.MortarRecipe;
import tnt.blockychef.common.food.recipe.MultiIngredient;
import tnt.blockychef.common.init.BlockyChefBlocks;

import java.util.List;

public class DoughMakerRecipeCategory extends BlockyChefRecipeCategory<DoughMakerRecipe> {

    private final IDrawableAnimated arrow;

    public DoughMakerRecipeCategory(IGuiHelper helper) {
        super(helper, "dough_maker");
        this.arrow = helper.drawableBuilder(backgroundPath, 162, 0, 26, 12).setTextureSize(188, 36)
                .buildAnimated(300, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    protected IDrawable createBackground(IGuiHelper helper) {
        return helper.drawableBuilder(backgroundPath, 0, 0, 162, 36).setTextureSize(188, 36)
                .build();
    }

    @Override
    protected ItemStack getIconStack() {
        return new ItemStack(BlockyChefBlocks.DOUGH_MAKER);
    }

    @Override
    public void draw(DoughMakerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 68, 12);
        drawCentered(getTimeLabel(recipe.getProcessingTime()).getString(), guiGraphics, 0, 3, 0x808080, false);
        float exp = recipe.getExperience();
        if (exp > 0) {
            drawCentered(getExperienceLabel(exp).getString(), guiGraphics, 0, 25, 0x808080, false);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DoughMakerRecipe recipe, IFocusGroup focuses) {
        List<List<ItemStack>> inputs = recipe.getInputs().stream()
                .map(MultiIngredient::toItemStackList).toList();
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                int index = x + y * 3;
                if (index >= inputs.size())
                    break;
                builder.addSlot(RecipeIngredientRole.INPUT, 1 + x * 18, 1 + y * 18)
                        .addItemStacks(inputs.get(index));
            }
        }
        ItemStack[] outputs = recipe.getOutputs();
        for (int x = 0; x < Math.min(3, outputs.length); x++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 109 + x * 18, 10).addItemStack(outputs[x]);
        }
    }

    @Override
    public RecipeType<DoughMakerRecipe> getRecipeType() {return JeiIntegrationPlugin.DOUGH_MAKER;}
}
