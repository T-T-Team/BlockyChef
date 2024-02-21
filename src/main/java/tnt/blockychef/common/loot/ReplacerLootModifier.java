package tnt.blockychef.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class ReplacerLootModifier extends LootModifier {

    public static final Codec<ReplacerLootModifier> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance).and(
            instance.group(
                    ResourceLocation.CODEC.fieldOf("targetTable").forGetter(t -> t.targetTable),
                    Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(t -> t.chance),
                    ItemStack.CODEC.fieldOf("replacementDrop").forGetter(t -> t.droppedItem)
            )
    ).apply(instance, ReplacerLootModifier::new));

    private final ResourceLocation targetTable;
    private final float chance;
    private final ItemStack droppedItem;

    public ReplacerLootModifier(LootItemCondition[] conditionsIn, ResourceLocation targetTable, float chance, ItemStack droppedItem) {
        super(conditionsIn);
        this.targetTable = targetTable;
        this.chance = chance;
        this.droppedItem = droppedItem;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ResourceLocation contextTable = context.getQueriedLootTableId();
        if (this.targetTable.equals(contextTable)) {
            RandomSource random = context.getRandom();
            if (random.nextFloat() >= this.chance) {
                generatedLoot.clear();
                generatedLoot.add(this.droppedItem.copy());
            }
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
