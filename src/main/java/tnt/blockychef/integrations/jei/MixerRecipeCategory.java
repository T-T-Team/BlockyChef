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
import tnt.blockychef.common.block.entity.MixerBlockEntity;
import tnt.blockychef.common.food.recipe.MixerRecipe;
import tnt.blockychef.common.food.recipe.MultiIngredient;
import tnt.blockychef.common.init.BlockyChefBlocks;

import java.util.List;

public class MixerRecipeCategory extends BlockychefFluidRecipeCategory<MixerRecipe> {

    private final IDrawableAnimated progressArrow;

    public MixerRecipeCategory(IGuiHelper helper) {
        super(helper, "mixer");
        this.progressArrow = helper.drawableBuilder(backgroundPath, 126, 0, 26, 12).setTextureSize(152, 36)
                .buildAnimated(300, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    protected IDrawable createBackground(IGuiHelper helper) {
        return helper.drawableBuilder(backgroundPath, 0, 0, 126, 36).setTextureSize(152, 36)
                .build();
    }

    @Override
    protected ItemStack getIconStack() {
        return new ItemStack(BlockyChefBlocks.MIXER);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MixerRecipe recipe, IFocusGroup focuses) {
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
        FluidStack stack = recipe.getOutput();

        builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 10).addFluidStack(stack.getFluid(), adjustToCapacity(stack.getAmount(), MixerBlockEntity.FLUID_CAPACITY));
    }

    @Override
    public void draw(MixerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        fluidIconsOverlay.draw(guiGraphics, 109, 10);
        progressArrow.draw(guiGraphics, 68, 12);
        drawCentered(recipe.getRpm().name(), guiGraphics, 18, 1, 0x808080, false);
        drawCentered(getValueLabel(recipe.getOutput().getAmount()).getString(), guiGraphics, 18, 28, 0x808080, false);
    }

    @Override
    public RecipeType<MixerRecipe> getRecipeType() {
        return JeiIntegrationPlugin.MIXER;
    }
}
