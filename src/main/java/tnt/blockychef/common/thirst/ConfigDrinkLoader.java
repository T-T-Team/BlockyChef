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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.init.BlockyChefMobEffects;
import tnt.blockychef.util.CodecHelper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

public final class ConfigDrinkLoader {

    public static final Marker MARKER = MarkerManager.getMarker("DrinkProviderLoader");
    private static final File FILE = new File("./config/blockychef/drinks.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Map<Item, DrinkProperties.DrinkPropertiesHolder> LOADED_STATS = new HashMap<>();
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
                Optional<JsonElement> optional = dataResult.resultOrPartial(str -> BlockyChef.LOGGER.error(MARKER, str));
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
            Optional<List<CompatDrinkable>> optional = dataResult.resultOrPartial(str -> BlockyChef.LOGGER.error(MARKER, str));
            optional.ifPresent(list -> list.forEach(drinkable -> LOADED_STATS.put(drinkable.item(), drinkable.holder().asDrinkPropertyHolder())));
        } catch (IOException e) {
            throw new RuntimeException("Drink file load failed", e);
        }
    }

    public static Optional<DrinkProperties.DrinkPropertiesHolder> getStatsHolder(Item item) {
        return Optional.ofNullable(LOADED_STATS.get(item));
    }

    private static void initVanillaDrinkables(List<CompatDrinkable> list) {
        new DefaultDrinkBuilder(Items.POTION)
                .stats(2)
                .addEffect(0.3F, BlockyChefMobEffects.THIRST)
                .buildAndExport(list);
        new DefaultDrinkBuilder(Items.COOKED_BEEF)
                .hydrationLoss(2)
                .buildAndExport(list);

        // For testing only, TODO rework
        new DefaultDrinkBuilder(Items.MILK_BUCKET)
                .stats(30)
                .buildAndExport(list);
        new DefaultDrinkBuilder(Items.MELON_SLICE)
                .stats(5, 10)
                .buildAndExport(list);
    }

    private record CompatDrinkable(Item item, CompatDrinkStatsHolder holder) {

        public static final Codec<CompatDrinkable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CodecHelper.registryEntry(ForgeRegistries.ITEMS).fieldOf("item").forGetter(CompatDrinkable::item),
                CompatDrinkStatsHolder.CODEC.fieldOf("stats").forGetter(CompatDrinkable::holder)
        ).apply(instance, CompatDrinkable::new));
    }

    private record CompatDrinkStatsHolder(int hydrationLevel, float saturation, List<RandomEffect> effectChances) {

            public static final Codec<CompatDrinkStatsHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("hydration").forGetter(t -> t.hydrationLevel),
                    Codec.FLOAT.fieldOf("saturation").forGetter(t -> t.saturation),
                    RandomEffect.CODEC.listOf().optionalFieldOf("effects", Collections.emptyList()).forGetter(t -> t.effectChances)
            ).apply(instance, CompatDrinkStatsHolder::new));

        public DrinkProperties.DrinkPropertiesHolder asDrinkPropertyHolder() {
                DrinkProperties properties = DrinkProperties.Builder.create()
                        .stats(hydrationLevel, saturation)
                        .onDrink(player -> {
                            RandomSource source = player.getRandom();
                            effectChances.forEach(eff -> {
                                if (source.nextFloat() < eff.chance()) {
                                    player.addEffect(eff.provider().get());
                                }
                            });
                        })
                        .build();
                ItemStack returning = ItemStack.EMPTY;
                return new DrinkProperties.DrinkPropertiesHolder(properties, returning);
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
            return new MobEffectInstance(effect, duration, amplifier);
        }
    }

    private static final class DefaultDrinkBuilder {

        private final Item target;
        private int hydration;
        private float saturation;
        private final List<RandomEffect> effects = new ArrayList<>();

        public DefaultDrinkBuilder(Item target) {
            this.target = target;
        }

        public DefaultDrinkBuilder stats(int hydration, float saturation) {
            this.hydration = hydration;
            this.saturation = saturation;
            return this;
        }

        public DefaultDrinkBuilder stats(int hydration, int saturation) {
            return stats(hydration, DrinkProperties.calculateSaturationForHydrationLevel(hydration, saturation));
        }

        public DefaultDrinkBuilder stats(int hydration) {
            return stats(hydration, hydration);
        }

        public DefaultDrinkBuilder hydrationLoss(int hydration) {
            return stats(-hydration, 0.0F);
        }

        public DefaultDrinkBuilder addEffect(float chance, MobEffect effect, int duration, int amplifier) {
            this.effects.add(new RandomEffect(chance, new EffectProvider(effect, duration, amplifier)));
            return this;
        }

        public DefaultDrinkBuilder addEffect(float chance, MobEffect effect, int duration) {
            return this.addEffect(chance, effect, duration, 0);
        }

        public DefaultDrinkBuilder addEffect(float chance, MobEffect effect) {
            return this.addEffect(chance, effect, 600);
        }

        public void buildAndExport(List<CompatDrinkable> out) {
            CompatDrinkable drinkable = new CompatDrinkable(this.target, new CompatDrinkStatsHolder(this.hydration, this.saturation, this.effects));
            out.add(drinkable);
        }
    }
}
