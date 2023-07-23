package tnt.blockychef.integrations.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.food.recipe.DryingRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;

import java.util.List;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {

    public static final ResourceLocation PLUGIN_ID = new ResourceLocation(BlockyChef.MODID, "jei_integration");

    static final RecipeType<DryingRecipe> DRYING_RECIPE = new RecipeType<>(BlockyChef.resource("drying"), DryingRecipe.class);

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(DRYING_RECIPE, getRecipes(BlockyChefRecipeTypes.DRYING_RECIPE));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new DryingRecipeCategory(helper));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.DRYING_RACK), DRYING_RECIPE);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {

    }

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    private static <I extends Container, R extends Recipe<I>> List<R> getRecipes(net.minecraft.world.item.crafting.RecipeType<R> type) {
        Level level = Minecraft.getInstance().level;
        RecipeManager manager = level.getRecipeManager();
        return manager.getAllRecipesFor(type);
    }
}
