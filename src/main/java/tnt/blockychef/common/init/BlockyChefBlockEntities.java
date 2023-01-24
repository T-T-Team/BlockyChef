package tnt.blockychef.common.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;
import tnt.blockychef.common.block.entity.DryingRackBlockEntity;

public final class BlockyChefBlockEntities {

    @ObjectHolder(value = "blockychef:drying_rack", registryName = "block_entity_type")
    public static final BlockEntityType<DryingRackBlockEntity> DRYING_RACK = null;
}
