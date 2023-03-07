package tnt.blockychef.common.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;
import tnt.blockychef.common.block.entity.CookingTableBlockEntity;
import tnt.blockychef.common.block.entity.DryingRackBlockEntity;
import tnt.blockychef.common.block.entity.KitchenCabinetBlockEntity;
import tnt.blockychef.common.block.entity.KitchenCounterBlockEntity;

public final class BlockyChefBlockEntities {

    @ObjectHolder(value = "blockychef:drying_rack", registryName = "block_entity_type")
    public static final BlockEntityType<DryingRackBlockEntity> DRYING_RACK = null;
    @ObjectHolder(value = "blockychef:kitchen_counter", registryName = "block_entity_type")
    public static final BlockEntityType<KitchenCounterBlockEntity> KITCHEN_COUNTER = null;
    @ObjectHolder(value = "blockychef:kitchen_cabinet", registryName = "block_entity_type")
    public static final BlockEntityType<KitchenCabinetBlockEntity> KITCHEN_CABINET = null;
    @ObjectHolder(value = "blockychef:cooking_table", registryName = "block_entity_type")
    public static final BlockEntityType<CookingTableBlockEntity> COOKING_TABLE = null;
}
