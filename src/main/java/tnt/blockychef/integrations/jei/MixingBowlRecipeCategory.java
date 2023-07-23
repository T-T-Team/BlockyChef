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
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import tnt.blockychef.common.food.recipe.MixingBowlRecipe;
import tnt.blockychef.common.food.recipe.MultiIngredient;
import tnt.blockychef.common.init.BlockyChefBlocks;

import java.util.List;

public class MixingBowlRecipeCategory extends BlockyChefRecipeCategory<MixingBowlRecipe> {

    private final IDrawableAnimated arrow;

    public MixingBowlRecipeCategory(IGuiHelper helper) {
        super(helper, "mixing_bowl");
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
        return new ItemStack(BlockyChefBlocks.OAK_MIXING_BOWL);
    }

    @Override
    public void draw(MixingBowlRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 68, 12);
        drawCentered(getTimeLabel(recipe.getMixingTime()).getString(), guiGraphics, 0, 3, 0x808080, false);
        drawCentered(getExperienceLabel(recipe.getExperience()).getString(), guiGraphics, 0, 25, 0x808080, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MixingBowlRecipe recipe, IFocusGroup focuses) {
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
    public RecipeType<MixingBowlRecipe> getRecipeType() {
        return JeiIntegrationPlugin.MIXING_BOWL;
    }
}
