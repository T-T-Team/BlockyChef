package tnt.blockychef.integrations.jei;

import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import tnt.blockychef.common.food.recipe.AbstractFoodRecipe;

public abstract class BlockyChefRecipeCategory<T extends AbstractFoodRecipe<?>> implements IRecipeCategory<T> {

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
