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
import net.minecraftforge.fluids.FluidStack;
import tnt.blockychef.common.block.entity.JuicerBlockEntity;
import tnt.blockychef.common.food.recipe.JuicerRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;

public class JuicerRecipeCategory extends BlockychefFluidRecipeCategory<JuicerRecipe> {

    private final IDrawableAnimated progressArrow;

    public JuicerRecipeCategory(IGuiHelper helper) {
        super(helper, "juicer");
        progressArrow = helper.drawableBuilder(backgroundPath, 72, 0, 22, 16).setTextureSize(94, 18)
                .buildAnimated(300, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    protected IDrawable createBackground(IGuiHelper helper) {
        return helper.drawableBuilder(backgroundPath, 0, 0, 72, 18).setTextureSize(94, 18).build();
    }

    @Override
    protected ItemStack getIconStack() {
        return new ItemStack(BlockyChefBlocks.JUICER);
    }

    @Override
    public RecipeType<JuicerRecipe> getRecipeType() {
        return JeiIntegrationPlugin.JUICER;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, JuicerRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.getInput());
        FluidStack stack = recipe.getOutput();
        builder.addSlot(RecipeIngredientRole.OUTPUT, 55, 1).addFluidStack(stack.getFluid(), adjustToCapacity(stack.getAmount(), JuicerBlockEntity.FLUID_CAPACITY));
    }

    @Override
    public void draw(JuicerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        fluidIconsOverlay.draw(guiGraphics, 55, 1);
        progressArrow.draw(guiGraphics, 25, 1);
        Font font = Minecraft.getInstance().font;
        float top = (background.getHeight() - font.lineHeight) / 2.0F;
        guiGraphics.drawString(font, getValueLabel(recipe.getOutput().getAmount()).getString(), background.getWidth() + 5, top, 0x808080, false);
    }
}
