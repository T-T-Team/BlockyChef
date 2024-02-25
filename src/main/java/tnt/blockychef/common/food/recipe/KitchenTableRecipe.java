package tnt.blockychef.common.food.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.block.entity.CookingTableBlockEntity;

import java.util.List;

public class KitchenTableRecipe extends AbstractFoodRecipe<CookingTableBlockEntity> {

    private final List<MultiIngredient> inputs;
    private final List<ItemStack> outputs;
    private int assemblyTime;

    public KitchenTableRecipe(List<MultiIngredient> inputs, List<ItemStack> outputs, int assemblyTime, List<MultiIngredient> inputConsumers, float experience) {
        super(inputConsumers, experience);
        this.inputs = inputs;
        this.outputs = outputs;
        this.assemblyTime = assemblyTime;
    }

    public List<MultiIngredient> getInputs() {
        return inputs;
    }

    public List<ItemStack> getOutputs() {
        return outputs;
    }

    public int getAssemblyTime() {
        return assemblyTime;
    }

    @Override
    public boolean matches(CookingTableBlockEntity pContainer, Level pLevel) {
        return MultiIngredient.test(pContainer, CookingTableBlockEntity.INPUTS, this.inputs);
    }

    @Override
    public ItemStack assemble(CookingTableBlockEntity pContainer, RegistryAccess pRegistryAccess) {
        return null;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }
}
