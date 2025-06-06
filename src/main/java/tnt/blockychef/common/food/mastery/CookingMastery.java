package tnt.blockychef.common.food.mastery;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.registries.ForgeRegistries;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.message.S2C_SendMasteryLevelUpEvent;
import tnt.tntlib.api.serialization.Codecs;

import java.util.*;

public record CookingMastery(Item item, List<Tier> tiers, List<MasteryGroup> groups) {

    public static final String QUALITY_TAG_KEY = "blockychef.quality";
    public static final List<Tier> DEFAULT_TIER_LIST = ImmutableList.<Tier>builder()
            .add(
                    new Tier(Tier.Badge.NONE, 0, createQualitiesMap(0.45F, 0.15F, 0.0F, 0.0F), SoundEvents.EMPTY, 1.0F, 1.0F),
                    new Tier(Tier.Badge.BRONZE, 15, createQualitiesMap(0.55F, 0.30F, 0.15F, 0.0F), SoundEvents.ALLAY_HURT, 1.0F, 1.0F),
                    new Tier(Tier.Badge.SILVER, 35, createQualitiesMap(0.7F, 0.45F, 0.3F, 0.1F), SoundEvents.EMPTY, 1.0F, 1.0F),
                    new Tier(Tier.Badge.GOLD, 60, createQualitiesMap(1.0F, 0.65F, 0.45F, 0.15F), SoundEvents.EMPTY, 1.0F, 1.0F),
                    new Tier(Tier.Badge.EMERALD, 90, createQualitiesMap(1.0F, 0.85F, 0.6F, 0.25F), SoundEvents.EMPTY, 1.0F, 1.0F),
                    new Tier(Tier.Badge.DIAMOND, 130, createQualitiesMap(0.0F, 1.0F, 0.75F, 0.45F), SoundEvents.EMPTY, 1.0F, 1.0F)
            )
            .build();
    public static final Codec<CookingMastery> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(CookingMastery::item),
            Tier.TIER_CODEC.listOf().optionalFieldOf("tiers", DEFAULT_TIER_LIST).forGetter(CookingMastery::tiers),
            Codecs.enumCodec(MasteryGroup.class).listOf().optionalFieldOf("groups", Collections.singletonList(MasteryGroup.NONE)).forGetter(CookingMastery::groups)
    ).apply(instance, CookingMastery::new));

    public static void applyMastery(Player player, ItemStack stack) {
        CookingMasteryManager manager = BlockyChef.MASTERY_MANAGER;
        Item item = stack.getItem();
        manager.getMastery(item).ifPresent(cookingMastery -> PlayerMasteryDataProvider.getMasteryData(player).ifPresent(masteryDataProvider -> {
            int cookCounter = masteryDataProvider.getCookedCount(item);
            FoodQuality quality = cookingMastery.getItemQuality(cookCounter, player.getRandom());
            CompoundTag tag = stack.getOrCreateTag();
            tag.putInt(QUALITY_TAG_KEY, quality.ordinal());
            int count = stack.getCount();
            Tier tier = cookingMastery.getTier(cookCounter);
            Tier nextTier = cookingMastery.getTier(cookCounter + count);
            masteryDataProvider.addCookedCount(item, count);
            masteryDataProvider.sendClientData();
            if (tier != nextTier && player instanceof ServerPlayer serverPlayer) {
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
                NetworkManager.DISPATCHER.sendToClient(serverPlayer, new S2C_SendMasteryLevelUpEvent(id, cookCounter));
                player.level().playSound(player, player.blockPosition(), nextTier.sound(), SoundSource.PLAYERS, nextTier.volume(), nextTier.pitch());
            }
        }));
    }

    public static boolean hasCustomQuality(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains(QUALITY_TAG_KEY);
    }

    public static FoodQuality getItemQuality(ItemStack stack) {
        if (!hasCustomQuality(stack)) {
            return null;
        }
        int qualityId = stack.getTag().getInt(QUALITY_TAG_KEY);
        return FoodQuality.resolve(qualityId);
    }

    public FoodQuality getItemQuality(int cookCounter, RandomSource random) {
        Tier tier = getTier(cookCounter);
        FoodQuality[] qualities = FoodQuality.values();
        FoodQuality quality = FoodQuality.BAD;
        float rolled = random.nextFloat();
        for (int i = qualities.length - 1; i > 0; i--) {
            float qualityChance = tier.getChanceForQuality(qualities[i]);
            if (qualityChance > 0.0F && rolled < qualityChance) {
                quality = qualities[i];
                break;
            }
        }
        return quality;
    }

    public Tier getTier(int cookCounter) {
        Tier selectedTier = Tier.DEFAULT_TIER;
        for (Tier tier : tiers) {
            if (tier.cookAmount <= cookCounter) {
                selectedTier = tier;
            } else {
                break;
            }
        }
        return selectedTier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CookingMastery that = (CookingMastery) o;
        return Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item);
    }

    @Override
    public String toString() {
        return "CookingMastery{" +
                "item=" + item +
                '}';
    }

    public static Map<FoodQuality, Float> createQualitiesMap(float average, float good, float great, float perfect) {
        Map<FoodQuality, Float> map = new EnumMap<>(FoodQuality.class);
        map.put(FoodQuality.AVERAGE, average);
        map.put(FoodQuality.GOOD, good);
        map.put(FoodQuality.GREAT, great);
        map.put(FoodQuality.PERFECT, perfect);
        return map;
    }

    public static FoodProperties getAdjustedFoodProperties(FoodProperties original, ItemStack stack) {
        UseAnim useAnim = stack.getUseAnimation();
        if (useAnim != UseAnim.EAT) {
            return original;
        }
        FoodQuality quality = getItemQuality(stack);
        return quality != null ? quality.applyFood(original) : original;
    }

    public record Tier(Badge badge, int cookAmount, Map<FoodQuality, Float> qualityChancesMap, SoundEvent sound, float volume, float pitch) {

        public static final Codec<Tier> TIER_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codecs.enumCodec(Badge.class).optionalFieldOf("badge", Badge.NONE).forGetter(Tier::badge),
                Codec.INT.fieldOf("countCooked").forGetter(Tier::cookAmount),
                Codec.unboundedMap(
                        Codecs.enumCodec(FoodQuality.class),
                        Codec.FLOAT
                ).fieldOf("qualities").forGetter(Tier::qualityChancesMap),
                BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("sound").forGetter(Tier::sound),
                Codec.FLOAT.fieldOf("volume").forGetter(Tier::volume),
                Codec.FLOAT.fieldOf("pitch").forGetter(Tier::pitch)
        ).apply(instance, Tier::new));
        public static final Tier DEFAULT_TIER = new Tier(Badge.NONE, -1, createQualitiesMap(1.0F, 0.0F, 0.0F, 0.0F), SoundEvents.EMPTY, 1.0F, 1.0F);

        public float getChanceForQuality(FoodQuality quality) {
            return qualityChancesMap.getOrDefault(quality, 0.0F);
        }

        public enum Badge {

            NONE,
            BRONZE,
            SILVER,
            GOLD,
            EMERALD,
            DIAMOND;

            private final ResourceLocation icon;

            Badge() {
                this.icon = BlockyChef.resource("textures/icon/badge_" + name().toLowerCase(Locale.ROOT) + ".png");
            }

            public int getTexturePositionX() {
                int index = ordinal();
                return (32 * index) % 256;
            }

            public ResourceLocation getIconPath() {
                return icon;
            }
        }
    }
}
