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
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import tnt.blockychef.common.food.recipe.AbstractFoodRecipe;
import tnt.blockychef.util.MenuInventoryHelper;

import java.util.List;

public abstract class RecipeRemberingBlockEntity<R extends AbstractFoodRecipe<?>> extends InventoryBlockEntityWithContainerWrapper {

    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();

    public RecipeRemberingBlockEntity(BlockEntityType<? extends RecipeRemberingBlockEntity> type, BlockPos pos, BlockState state) {
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
        List<Recipe<?>> list = this.getRecipesToAwardAndPopExperience(player.getLevel(), player.position());
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

    private static void createExperience(ServerLevel level, Vec3 position, int count, float experience) {
        int i = Mth.floor((float)count * experience);
        float f = Mth.frac((float)count * experience);
        if (f != 0.0F && Math.random() < (double)f) {
            ++i;
        }

        ExperienceOrb.award(level, position, i);
    }
}
