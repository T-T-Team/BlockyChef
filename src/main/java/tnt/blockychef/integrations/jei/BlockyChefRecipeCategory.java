package tnt.blockychef.integrations.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.food.recipe.AbstractFoodRecipe;

public abstract class BlockyChefRecipeCategory<T extends AbstractFoodRecipe<?>> implements IRecipeCategory<T> {

    protected final ResourceLocation backgroundPath;
    protected IDrawable background;
    protected IDrawable icon;
    protected Component title;

    public BlockyChefRecipeCategory(IGuiHelper helper, String name) {
        this.backgroundPath = BlockyChef.resource(String.format("textures/screen/jei/%s.png", name));
        this.background = this.createBackground(helper);
        this.icon = helper.createDrawableItemStack(this.getIconStack());
        this.title = Component.translatable("jei.recipe_category.blockychef." + name);
    }

    protected abstract IDrawable createBackground(IGuiHelper helper);

    protected abstract ItemStack getIconStack();

    @Override
    public final IDrawable getBackground() {
        return background;
    }

    @Override
    public final IDrawable getIcon() {
        return icon;
    }

    @Override
    public final Component getTitle() {
        return title;
    }

    protected final void drawExperience(float experience, GuiGraphics guiGraphics, int x, int y) {
        if (experience > 0) {
            Component experienceString = Component.translatable("gui.jei.category.smelting.experience", experience);
            Font font = Minecraft.getInstance().font;
            guiGraphics.drawString(font, experienceString, x + getBackground().getWidth(), y, 0xFF808080, false);
        }
    }

    protected final void drawTime(int time, GuiGraphics guiGraphics, int x, int y) {
        if (time > 0) {
            int timeSeconds = time / 20;
            Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", timeSeconds);
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            guiGraphics.drawString(fontRenderer, timeString, x + getBackground().getWidth(), y, 0xFF808080, false);
        }
    }
}
