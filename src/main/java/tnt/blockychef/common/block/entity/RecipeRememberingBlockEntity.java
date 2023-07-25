package tnt.blockychef.common.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import tnt.blockychef.common.food.recipe.AbstractFoodRecipe;
import tnt.blockychef.util.MenuInventoryHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class RecipeRememberingBlockEntity<R extends AbstractFoodRecipe<?>> extends InventoryBlockEntityWithContainerWrapper {

    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();

    public RecipeRememberingBlockEntity(BlockEntityType<? extends RecipeRememberingBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        CompoundTag usedRecipesTag = tag.getCompound("recipesUsed");
        for (String key : usedRecipesTag.getAllKeys()) {
            this.recipesUsed.put(new ResourceLocation(key), usedRecipesTag.getInt(key));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        CompoundTag usedRecipesTag = new CompoundTag();
        this.recipesUsed.forEach((key, count) -> usedRecipesTag.putInt(key.toString(), count));
        tag.put("recipesUsed", usedRecipesTag);
    }

    public void awardUsedRecipesAndPopExperience(ServerPlayer player) {
        List<Recipe<?>> list = this.getRecipesToAwardAndPopExperience(player.serverLevel(), player.position());
        player.awardRecipes(list);
        this.recipesUsed.clear();
    }

    @SuppressWarnings("unchecked")
    public List<Recipe<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 position) {
        List<Recipe<?>> list = Lists.newArrayList();
        for(Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent(recipe -> {
                list.add(recipe);
                createExperience(level, position, entry.getIntValue(), ((R) recipe).getExperience());
            });
        }

        return list;
    }

    public void dropInventoryAndExp() {
        if (level.isClientSide)
            return;
        MenuInventoryHelper.dropInventoryContents(level, worldPosition, this);
        getRecipesToAwardAndPopExperience((ServerLevel) level, Vec3.atCenterOf(worldPosition));
    }

    public void storeRecipe(R recipe) {
        this.recipesUsed.addTo(recipe.getId(), 1);
    }

    protected void consumeIngredientsAndApplyCraftRemainder(int[] inputs, int[] outputs, Consumer<int[]> ingredientConsumer) {
        List<ItemStack> craftingRemainder = new ArrayList<>();
        for (int slot : inputs) {
            ItemStack stack = getItem(slot);
            if (!stack.isEmpty() && stack.hasCraftingRemainingItem()) {
                craftingRemainder.add(stack.getCraftingRemainingItem());
            }
        }
        ingredientConsumer.accept(inputs);
        int[] slots = new int[inputs.length + outputs.length];
        System.arraycopy(outputs, 0, slots, 0, outputs.length);
        System.arraycopy(inputs, 0, slots, outputs.length, inputs.length);
        for (ItemStack remainder : craftingRemainder) {
            if (MenuInventoryHelper.canFitItems(new ItemStack[] {remainder}, this, slots)) {
                MenuInventoryHelper.insertItems(new ItemStack[] {remainder.copy()}, this, slots);
            } else {
                Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, remainder.copy());
            }
        }
    }

    private static void createExperience(ServerLevel level, Vec3 position, int count, float experience) {
        int i = Mth.floor((float)count * experience);
        float f = Mth.frac((float)count * experience);
        if (f != 0.0F && Math.random() < (double)f) {
            ++i;
        }

        ExperienceOrb.award(level, position, i);
    }
}
