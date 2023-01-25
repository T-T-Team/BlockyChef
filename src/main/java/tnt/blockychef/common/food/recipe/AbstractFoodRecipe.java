package tnt.blockychef.common.food.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

public abstract class AbstractFoodRecipe<C extends Container> implements Recipe<C> {

    private final ResourceLocation id;
    private final float experience;

    public AbstractFoodRecipe(ResourceLocation id, float experience) {
        this.id = id;
        this.experience = experience;
    }

    public float getExperience() {
        return experience;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return true;
    }
}
