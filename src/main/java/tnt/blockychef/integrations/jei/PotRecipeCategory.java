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
import net.minecraft.world.level.material.Fluids;
import tnt.blockychef.common.block.entity.PotBlockEntity;
import tnt.blockychef.common.food.recipe.BaseCookConfiguration;
import tnt.blockychef.common.food.recipe.PanRecipe;
import tnt.blockychef.common.food.recipe.PotRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefFluids;
import tnt.tntlib.api.GraphicsHelper;
import tnt.tntlib.api.HorizontalAlignment;
import tnt.tntlib.api.VerticalAlignment;

public class PotRecipeCategory extends BlockychefFluidRecipeCategory<PotRecipe> {

    private final IDrawableAnimated flame;
    private final IDrawableAnimated arrow;

    public PotRecipeCategory(IGuiHelper helper) {
        super(helper, "pot");
        this.flame = helper.drawableBuilder(backgroundPath, 90, 16, 14, 14).setTextureSize(112, 56)
                .buildAnimated(200, IDrawableAnimated.StartDirection.BOTTOM, false);
        this.arrow = helper.drawableBuilder(backgroundPath, 90, 0, 22, 16).setTextureSize(112, 56)
                .buildAnimated(300, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    protected IDrawable createBackground(IGuiHelper helper) {
        return helper.drawableBuilder(backgroundPath, 0, 0, 90, 56).setTextureSize(112, 56).build();
    }

    @Override
    protected ItemStack getIconStack() {
        return new ItemStack(BlockyChefBlocks.POT);
    }

    @Override
    public RecipeType<PotRecipe> getRecipeType() {
        return JeiIntegrationPlugin.POT;
    }

    @Override
    public void draw(PotRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        flame.draw(guiGraphics, 18, 20);
        arrow.draw(guiGraphics, 33, 1);
        Font font = Minecraft.getInstance().font;
        fluidIconsOverlay.draw(guiGraphics, 1, 39);
        BaseCookConfiguration cfg = recipe.getConfiguration();
        GraphicsHelper.drawAlignedText(guiGraphics, getTimeLabel(cfg.time()), font, HorizontalAlignment.LEFT, VerticalAlignment.TOP, 0, 0, 90, 38, 0xFF808080, false, 0, 27);
        GraphicsHelper.drawAlignedText(guiGraphics, cfg.getTemperatureRange(), font, HorizontalAlignment.RIGHT, VerticalAlignment.TOP, 0, 0, 90, 38, 0xFF808080, false, 0, 27);
        GraphicsHelper.drawAlignedText(guiGraphics, getValueLabel(recipe.getConfiguration().minWaterLevel()), font, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, 0, 0, 100, 18, 0xFF808080, false, 20, 39);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PotRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 73, 1).addItemStack(recipe.getResult());

        builder.addSlot(RecipeIngredientRole.INPUT, 1, 39).addFluidStack(Fluids.WATER, adjustToCapacity(recipe.getConfiguration().minWaterLevel(), PotBlockEntity.WATER_CAPACITY));
    }
}
