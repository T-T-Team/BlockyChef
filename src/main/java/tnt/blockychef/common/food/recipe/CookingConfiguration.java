package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CookingConfiguration(int time, float minTemperature, float maxTemperature, float burnSpeed) implements BaseCookConfiguration {

    public static final Codec<CookingConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("time").forGetter(CookingConfiguration::time),
            Codec.FLOAT.fieldOf("minTemperature").forGetter(CookingConfiguration::minTemperature),
            Codec.FLOAT.fieldOf("maxTemperature").forGetter(CookingConfiguration::maxTemperature),
            Codec.FLOAT.optionalFieldOf("burnSpeed", 1.0F).forGetter(CookingConfiguration::burnSpeed)
    ).apply(instance, CookingConfiguration::new));
}
