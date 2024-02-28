package tnt.blockychef.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import tnt.blockychef.common.init.BlockyChefVanillaExtensions;

import java.util.List;
import java.util.function.Consumer;

public class LootItemGroup extends LootPoolSingletonContainer {

    public static final Codec<LootItemGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.holderByNameCodec().listOf().fieldOf("items").forGetter(t -> t.items)
    ).and(singletonFields(instance)).apply(instance, LootItemGroup::new));

    private final List<Holder<Item>> items;

    public LootItemGroup(List<Holder<Item>> items, int counts, int qualities, List<LootItemCondition> conditions, List<LootItemFunction> functions) {
        super(counts, qualities, conditions, functions);
        this.items = items;
    }

    @Override
    protected void createItemStack(Consumer<ItemStack> pStackConsumer, LootContext pLootContext) {
        for (Holder<Item> item : items) {
            pStackConsumer.accept(new ItemStack(item));
        }
    }

    @Override
    public LootPoolEntryType getType() {
        return BlockyChefVanillaExtensions.LOOT_GROUP_ITEMS;
    }
}
