package tnt.blockychef.common.food.recipe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.block.entity.CuttingBoardBlockEntity;
import tnt.blockychef.common.food.RecipeProcessingType;
import tnt.blockychef.common.food.RecipeProcessingTypes;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.SerializationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CuttingBoardRecipe extends AbstractFoodRecipe<CuttingBoardBlockEntity> {

    private final Ingredient input;
    private final Map<RecipeProcessingType, CuttingBoardSubRecipe> subRecipeMap;

    public CuttingBoardRecipe(ResourceLocation id, Ingredient input, Map<RecipeProcessingType, CuttingBoardSubRecipe> subRecipeMap, float experience) {
        super(id, experience);
        this.input = input;
        this.subRecipeMap = subRecipeMap;
    }

    public CuttingBoardSubRecipe getSubRecipe(RecipeProcessingType recipeProcessingType) {
        return subRecipeMap.get(recipeProcessingType);
    }

    public RecipeProcessingType getFirstProcessingType() {
        return getRecipeProcessingTypes().get(0);
    }

    public List<RecipeProcessingType> getRecipeProcessingTypes() {
        return ImmutableList.copyOf(subRecipeMap.keySet());
    }

    public boolean isValidInput(ItemStack stack) {
        return this.input.test(stack);
    }

    @Override
    public boolean matches(CuttingBoardBlockEntity blockEntity, Level level) {
        ItemStack stack = blockEntity.getInputItem();
        return isValidInput(stack);
    }

    @Override
    public ItemStack assemble(CuttingBoardBlockEntity board) {
        throw new UnsupportedOperationException(); // TODO implement
    }

    @Override
    public ItemStack getResultItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.CUTTING_BOARD_RECIPE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.CUTTING_BOARD_RECIPE_SERIALIZER;
    }

    public static final class CuttingBoardSubRecipe {

        private final ItemStack[] outputs;
        private final int time;

        public CuttingBoardSubRecipe(ItemStack[] outputs, int time) {
            this.outputs = outputs;
            this.time = time;
        }

        public static Pair<RecipeProcessingType, CuttingBoardSubRecipe> fromJson(JsonObject entry) {
            ResourceLocation location = new ResourceLocation(GsonHelper.getAsString(entry, "recipeProcessingType"));
            RecipeProcessingType type = RecipeProcessingTypes.getById(location)
                    .orElseThrow(() -> new JsonSyntaxException("Unknown recipe processing type: " + location));
            JsonArray outputs = GsonHelper.getAsJsonArray(entry, "outputs");
            ItemStack[] outputItems = SerializationHelper.mapJsonArray(outputs, ItemStack[]::new,
                    el -> SerializationHelper.resolveItemStackFromJson(SerializationHelper.asObject(el)));
            if (outputItems.length > CuttingBoardBlockEntity.SLOT_OUTPUTS.length) {
                throw new JsonSyntaxException("Too many outputs defined. Got: " + outputItems.length + ", max is " + CuttingBoardBlockEntity.SLOT_OUTPUTS.length);
            }
            int time = GsonHelper.getAsInt(entry, "time");
            return Pair.of(type, new CuttingBoardSubRecipe(outputItems, time));
        }

        public int getTime() {
            return time;
        }

        public ItemStack[] getOutputs() {
            return outputs;
        }

        public void encode(FriendlyByteBuf buffer) {
            buffer.writeInt(time);
            buffer.writeInt(outputs.length);
            for (ItemStack stack : outputs) {
                buffer.writeItem(stack);
            }
        }

        public static CuttingBoardSubRecipe decode(FriendlyByteBuf buffer) {
            int time = buffer.readInt();
            int length = buffer.readInt();
            ItemStack[] outputs = new ItemStack[length];
            for (int i = 0; i < length; i++) {
                outputs[i] = buffer.readItem();
            }
            return new CuttingBoardSubRecipe(outputs, time);
        }
    }

    public static final class Serializer implements RecipeSerializer<CuttingBoardRecipe> {

        @Override
        public CuttingBoardRecipe fromJson(ResourceLocation recipeId, JsonObject data) {
            float experience = GsonHelper.getAsFloat(data, "experience", 0.0F);
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(data, "input"));
            JsonArray processingTypesJson = GsonHelper.getAsJsonArray(data, "values");
            if (processingTypesJson.size() == 0) {
                throw new JsonSyntaxException("Recipe " + recipeId + " must define atleast 1 recipe processing type");
            }
            List<Pair<RecipeProcessingType, CuttingBoardSubRecipe>> processingTypes = new ArrayList<>();
            for (JsonElement type : processingTypesJson) {
                JsonObject object = SerializationHelper.asObject(type);
                Pair<RecipeProcessingType, CuttingBoardSubRecipe> pair = CuttingBoardSubRecipe.fromJson(object);
                processingTypes.add(pair);
            }
            Map<RecipeProcessingType, CuttingBoardSubRecipe> subRecipeMap = processingTypes.stream()
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
            return new CuttingBoardRecipe(recipeId, ingredient, subRecipeMap, experience);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CuttingBoardRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeFloat(recipe.getExperience());
            buffer.writeInt(recipe.subRecipeMap.size());
            for (Map.Entry<RecipeProcessingType, CuttingBoardSubRecipe> entry : recipe.subRecipeMap.entrySet()) {
                buffer.writeResourceLocation(entry.getKey().getLocation());
                entry.getValue().encode(buffer);
            }
        }

        @Override
        public @Nullable CuttingBoardRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            float experience = buffer.readFloat();
            int count = buffer.readInt();
            Map<RecipeProcessingType, CuttingBoardSubRecipe> map = new HashMap<>();
            for (int i = 0; i < count; i++) {
                ResourceLocation location = buffer.readResourceLocation();
                RecipeProcessingType type = RecipeProcessingTypes.getById(location)
                        .orElseThrow(IllegalStateException::new);
                CuttingBoardSubRecipe subRecipe = CuttingBoardSubRecipe.decode(buffer);
                map.put(type, subRecipe);
            }
            return new CuttingBoardRecipe(recipeId, input, ImmutableMap.copyOf(map), experience);
        }
    }
}
