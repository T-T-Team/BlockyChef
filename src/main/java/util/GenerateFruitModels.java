package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class GenerateFruitModels {

    private static final String FRUIT_NAME = "coconut";
    private static final String FRUIT_SUFFIX = "_fruit";
    private static final int LOOT_BONUS = 2;
    private static final ModelVariant MODEL_VARIANT = ModelVariant.SMALL;
    private static final Runnable[] TASKS = {
            GenerateFruitModels::createBlockstateFile,
            GenerateFruitModels::createBlockModelFile,
            GenerateFruitModels::createItemModelFile,
            GenerateFruitModels::createLootTableFile
    };

    private static final File ASSETS_DIRECTORY = new File("./src/main/resources/assets/blockychef");
    private static final File DATA_DIRECTORY = new File("./src/main/resources/data/blockychef");
    private static final File ITEM_MODELS = new File(ASSETS_DIRECTORY, "models/item");
    private static final File BLOCK_MODELS = new File(ASSETS_DIRECTORY, "models/block");
    private static final File BLOCKSTATES = new File(ASSETS_DIRECTORY, "blockstates");
    private static final File BLOCK_LOOT_TABLES = new File(DATA_DIRECTORY, "loot_tables/blocks");
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    public static void main(String[] args) {
        Arrays.stream(TASKS).forEach(Runnable::run);
    }

    private static void createBlockstateFile() {
        JsonObject rootObject = new JsonObject();
        JsonObject variants = new JsonObject();
        rootObject.add("variants", variants);
        List<Variant> variantsList = new ArrayList<>();
        for (int fruitAge = 0; fruitAge < 3; fruitAge++) {
            String modelPath = FRUIT_NAME + "/" + FRUIT_NAME + "_" + fruitAge;
            Variant variant = new Variant(fruitAge, modelPath);
            variantsList.add(variant);
        }
        variantsList.forEach(variant -> {
            JsonObject modelVariantJson = new JsonObject();
            modelVariantJson.addProperty("model", "blockychef:block/" + variant.path);
            String propKey = String.format("age=%d", variant.age);
            variants.add(propKey, modelVariantJson);
        });
        try {
            File file = new File(BLOCKSTATES, FRUIT_NAME + FRUIT_SUFFIX + ".json");
            file.createNewFile();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(GSON.toJson(rootObject));
            }
        } catch (IOException e) {
            throw new RuntimeException("Blockstate file create failed", e);
        }
    }

    private static void createBlockModelFile() {
        for (int fruitAge = 0; fruitAge < 3; fruitAge++) {
            File modelFile = new File(BLOCK_MODELS, FRUIT_NAME + "/" + FRUIT_NAME + "_" + fruitAge + ".json");
            try {
                modelFile.getParentFile().mkdirs();
                modelFile.createNewFile();
                JsonObject object = new JsonObject();
                object.addProperty("parent", "blockychef:block/" + MODEL_VARIANT.path);
                object.addProperty("render_type", "cutout");
                JsonObject texturesJson = new JsonObject();
                texturesJson.addProperty("fruit", "blockychef:block/" + FRUIT_NAME + fruitAge);
                object.add("textures", texturesJson);
                try (FileWriter writer = new FileWriter(modelFile)) {
                    writer.write(GSON.toJson(object));
                }
            } catch (IOException e) {
                throw new RuntimeException("Block model create failed", e);
            }
        }
    }

    private static void createItemModelFile() {
        File itemModelFile = new File(ITEM_MODELS, FRUIT_NAME + ".json");
        try {
            itemModelFile.createNewFile();
            JsonObject modelContent = new JsonObject();
            modelContent.addProperty("parent", "item/generated");
            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", "blockychef:item/" + FRUIT_NAME);
            modelContent.add("textures", textures);
            String rawContent = GSON.toJson(modelContent);
            try (FileWriter writer = new FileWriter(itemModelFile)) {
                writer.write(rawContent);
            }
        } catch (IOException e) {
            throw new RuntimeException("Item model create failed", e);
        }
    }

    private static void createLootTableFile() {
        File file = new File(BLOCK_LOOT_TABLES, FRUIT_NAME + FRUIT_SUFFIX + ".json");
        if (file.exists())
            return;
        try {
            file.createNewFile();
            JsonObject root = new JsonObject();
            root.addProperty("type", "minecraft:block");
            JsonArray functions = new JsonArray();
            JsonObject funtion1 = new JsonObject();
            funtion1.addProperty("function", "minecraft:explosion_decay");
            functions.add(funtion1);
            root.add("functions", functions);
            JsonArray pools = new JsonArray();
            root.add("pools", pools);
            JsonObject pool1 = new JsonObject();
            pool1.addProperty("rolls", 1);
            pool1.addProperty("bonus_rolls", 0);
            JsonArray pool1entries = new JsonArray();
            pool1.add("entries", pool1entries);
            JsonObject pool1entry1 = new JsonObject();
            pool1entry1.addProperty("type", "minecraft:item");
            pool1entry1.addProperty("name", "blockychef:" + FRUIT_NAME);
            pool1entries.add(pool1entry1);
            pools.add(pool1);
            JsonObject pool2 = new JsonObject();
            pool2.addProperty("rolls", 1);
            pool2.addProperty("bonus_rolls", 0);
            JsonArray pool2conditions = new JsonArray();
            JsonObject pool2condition = new JsonObject();
            pool2condition.addProperty("condition", "minecraft:block_state_property");
            pool2condition.addProperty("block", "blockychef:" + FRUIT_NAME + FRUIT_SUFFIX);
            JsonObject pool2conditionProps = new JsonObject();
            pool2conditionProps.addProperty("age", "2");
            pool2condition.add("properties", pool2conditionProps);
            pool2conditions.add(pool2condition);
            pool2.add("conditions", pool2conditions);
            JsonArray pool2entries = new JsonArray();
            JsonObject pool2entry = new JsonObject();
            pool2entry.addProperty("type", "minecraft:item");
            JsonArray pool2entryFns = new JsonArray();
            JsonObject pool2Fn = new JsonObject();
            pool2Fn.addProperty("function", "minecraft:apply_bonus");
            pool2Fn.addProperty("enchantment", "minecraft:fortune");
            pool2Fn.addProperty("formula", "minecraft:binomial_with_bonus_count");
            JsonObject pool2FnParam = new JsonObject();
            pool2FnParam.addProperty("extra", LOOT_BONUS);
            pool2FnParam.addProperty("probability", 0.5714286);
            pool2Fn.add("parameters", pool2FnParam);
            pool2entryFns.add(pool2Fn);
            pool2entry.add("functions", pool2entryFns);
            pool2entry.addProperty("name", "blockychef:" + FRUIT_NAME);
            pool2entries.add(pool2entry);
            pool2.add("entries", pool2entries);
            pools.add(pool2);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(GSON.toJson(root));
            }
        } catch (IOException e) {
            throw new RuntimeException("Loot table create failed", e);
        }
    }

    private enum ModelVariant {

        SMALL("tree_fruit_small"),
        MEDIUM("tree_fruit_medium"),
        LARGE("tree_fruit_large");

        private final String path;

        ModelVariant(String path) {
            this.path = path;
        }
    }

    private record Variant(int age, String path) {}
}
