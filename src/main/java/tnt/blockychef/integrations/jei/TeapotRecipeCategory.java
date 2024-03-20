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
import net.minecraftforge.fluids.FluidStack;
import tnt.tntlib.api.ColorPalette;
import tnt.blockychef.common.block.entity.TeapotBlockEntity;
import tnt.blockychef.common.food.recipe.MultiIngredient;
import tnt.blockychef.common.food.recipe.TeapotRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;

import java.util.List;

public class TeapotRecipeCategory extends BlockychefFluidRecipeCategory<TeapotRecipe> {

    private final IDrawableAnimated progressArrow;

    public TeapotRecipeCategory(IGuiHelper helper) {
        super(helper, "teapot");
        this.progressArrow = helper.drawableBuilder(backgroundPath, 126, 0, 26, 12).setTextureSize(152, 62)
                .buildAnimated(300, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    protected IDrawable createBackground(IGuiHelper helper) {
        return helper.drawableBuilder(backgroundPath, 0, 0, 126, 62).setTextureSize(152, 62)
                .build();
    }

    @Override
    protected ItemStack getIconStack() {
        return new ItemStack(BlockyChefBlocks.TEAPOT);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TeapotRecipe recipe, IFocusGroup focuses) {
        List<List<ItemStack>> inputs = recipe.getInputs().stream()
                .map(MultiIngredient::toItemStackList).toList();
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                int index = x + y * 3;
                if (index >= inputs.size())
                    break;
                List<ItemStack> input = inputs.get(index);
                builder.addSlot(RecipeIngredientRole.INPUT, 1 + x * 18, 1 + y * 18)
                        .addItemStacks(input);
            }
        }
        FluidStack input = recipe.getBaseFluid();
        builder.addSlot(RecipeIngredientRole.INPUT, 19, 45).addFluidStack(input.getFluid(), adjustToCapacity(input.getAmount(), TeapotBlockEntity.CAPACITY));

        FluidStack result = recipe.getResult();
        builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 10).addFluidStack(result.getFluid(), adjustToCapacity(result.getAmount(), TeapotBlockEntity.CAPACITY));
    }

    @Override
    public void draw(TeapotRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        fluidIconsOverlay.draw(guiGraphics, 109, 10);
        fluidIconsOverlay.draw(guiGraphics, 19, 45);
        progressArrow.draw(guiGraphics, 68, 12);

        drawCentered(getValueLabel(recipe.getResult().getAmount()).getString(), guiGraphics, 18, 28, ColorPalette.GUI_TEXT_LIGHT_GRAY, false);
        drawCentered(getValueLabel(recipe.getBaseFluid().getAmount()).getString(), guiGraphics, -3, 49, ColorPalette.GUI_TEXT_LIGHT_GRAY, false);
    }

    @Override
    public RecipeType<TeapotRecipe> getRecipeType() {
        return JeiIntegrationPlugin.TEAPOT;
    }
}
