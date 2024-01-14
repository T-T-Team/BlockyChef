package tnt.blockychef.common.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import tnt.blockychef.BlockyChef;

public final class BlockyChefTags {

    public static final class Blocks {
        public static final TagKey<Block> WEAK_CONSTANT_HEAT = blockTag("heat/weak_constant");
        public static final TagKey<Block> STRONG_CONSTANT_HEAT = blockTag("heat/strong_constant");
        public static final TagKey<Block> WEAK_VARIABLE_HEAT = blockTag("heat/weak_variable");
        public static final TagKey<Block> STRONG_VARIABLE_HEAT = blockTag("heat/strong_variable");
    }

    public static final class Fluids {
        public static final TagKey<Fluid> HEATING_FLUIDS = fluidTag("heat/heating_fluids");
    }

    public static final class Items {
        public static final TagKey<Item> WATER = itemTag("water");
        public static final TagKey<Item> OIL = itemTag("oil");
    }

    private static TagKey<Block> blockTag(String path) {
        return BlockTags.create(new ResourceLocation(BlockyChef.MODID, path));
    }

    private static TagKey<Fluid> fluidTag(String path) {
        return FluidTags.create(new ResourceLocation(BlockyChef.MODID, path));
    }

    private static TagKey<Item> itemTag(String path) {
        return ItemTags.create(new ResourceLocation(BlockyChef.MODID, path));
    }
}
