package tnt.blockychef.common.thirst;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import tnt.blockychef.util.CodecHelper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

public final class DrinkLoader {

    private static final File FILE = new File("./config/blockychef/drinks.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Map<Item, DrinkStats> LOADED_STATS = new HashMap<>();
    private static final Codec<List<CompatDrinkable>> CODEC = CompatDrinkable.CODEC.listOf()
            .fieldOf("drinks").codec();

    public static void loadData() {
        LOADED_STATS.clear();
        try {
            File dir = FILE.getParentFile();
            dir.mkdirs();
            if (!FILE.exists()) {
                FILE.createNewFile();
                List<CompatDrinkable> defaultDrinks = new ArrayList<>();
                initVanillaDrinkables(defaultDrinks);
                DataResult<JsonElement> dataResult = CODEC.encodeStart(JsonOps.INSTANCE, defaultDrinks);
                Optional<JsonElement> optional = dataResult.result();
                if (optional.isPresent()) {
                    JsonElement element = optional.get();
                    String raw = GSON.toJson(element);
                    try (FileWriter writer = new FileWriter(FILE)) {
                        writer.write(raw);
                    }
                }
            }
            JsonElement element;
            try (FileReader reader = new FileReader(FILE)) {
                element = JsonParser.parseReader(reader);
            }
            DataResult<List<CompatDrinkable>> dataResult = CODEC.parse(JsonOps.INSTANCE, element);
            Optional<List<CompatDrinkable>> optional = dataResult.result();
            optional.ifPresent(list -> list.forEach(drinkable -> LOADED_STATS.put(drinkable.item(), drinkable.holder().toDrink())));
        } catch (IOException e) {
            throw new RuntimeException("Drink file load failed", e);
        }
    }

    public static Optional<DrinkStats> getStats(Item item) {
        return Optional.ofNullable(LOADED_STATS.get(item));
    }

    private static void initVanillaDrinkables(List<CompatDrinkable> list) {
        //list.add(new CompatDrinkStatsHolder(2, DrinkStats.calculateSaturationForWaterLevel(2, 2), new RandomEffect(0.3F, new EffectProvider())));
    }

    private record CompatDrinkable(Item item, CompatDrinkStatsHolder holder) {

        public static final Codec<CompatDrinkable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CodecHelper.registryEntry(ForgeRegistries.ITEMS).fieldOf("item").forGetter(CompatDrinkable::item),
                CompatDrinkStatsHolder.CODEC.fieldOf("stats").forGetter(CompatDrinkable::holder)
        ).apply(instance, CompatDrinkable::new));
    }

    private static final class CompatDrinkStatsHolder {

        public static final Codec<CompatDrinkStatsHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("hydration").forGetter(t -> t.hydrationLevel),
                Codec.FLOAT.fieldOf("saturation").forGetter(t -> t.saturation),
                RandomEffect.CODEC.listOf().optionalFieldOf("effects", Collections.emptyList()).forGetter(t -> t.effectChances)
        ).apply(instance, CompatDrinkStatsHolder::new));
        private final int hydrationLevel;
        private final float saturation;
        private final List<RandomEffect> effectChances;

        public CompatDrinkStatsHolder(int hydrationLevel, float saturation, List<RandomEffect> effectChances) {
            this.hydrationLevel = hydrationLevel;
            this.saturation = saturation;
            this.effectChances = effectChances;
        }

        public CompatDrinkStatsHolder(int hydrationLevel, float saturation, RandomEffect... effects) {
            this(hydrationLevel, saturation, Arrays.asList(effects));
        }

        public DrinkStats toDrink() {
            return new DrinkStats(this.hydrationLevel, this.saturation, player -> {
                RandomSource source = player.getRandom();
                this.effectChances.forEach(eff -> {
                    if (source.nextFloat() < eff.chance()) {
                        player.addEffect(eff.provider().get());
                    }
                });
            });
        }
    }

    private record RandomEffect(float chance, EffectProvider provider) {

        public static final Codec<RandomEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("chance").forGetter(RandomEffect::chance),
                EffectProvider.CODEC.fieldOf("effect").forGetter(RandomEffect::provider)
        ).apply(instance, RandomEffect::new));
    }

    private record EffectProvider(MobEffect effect, int duration, int amplifier) implements Supplier<MobEffectInstance> {

        public static final Codec<EffectProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CodecHelper.registryEntry(ForgeRegistries.MOB_EFFECTS).fieldOf("effect").forGetter(EffectProvider::effect),
                Codec.INT.optionalFieldOf("duration", 600).forGetter(EffectProvider::duration),
                Codec.INT.optionalFieldOf("amplifier", 0).forGetter(EffectProvider::amplifier)
        ).apply(instance, EffectProvider::new));

        @Override
        public MobEffectInstance get() {
            return new MobEffectInstance(this.effect, this.duration, this.amplifier);
        }
    }
}
