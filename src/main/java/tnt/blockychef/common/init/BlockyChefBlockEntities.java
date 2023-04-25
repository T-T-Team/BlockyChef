package tnt.blockychef.common.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;
import tnt.blockychef.common.block.KitchenSinkBlock;
import tnt.blockychef.common.block.entity.*;

public final class BlockyChefBlockEntities {

    @ObjectHolder(value = "blockychef:drying_rack", registryName = "block_entity_type")
    public static final BlockEntityType<DryingRackBlockEntity> DRYING_RACK = null;
    @ObjectHolder(value = "blockychef:kitchen_counter", registryName = "block_entity_type")
    public static final BlockEntityType<KitchenCounterBlockEntity> KITCHEN_COUNTER = null;
    @ObjectHolder(value = "blockychef:kitchen_cabinet", registryName = "block_entity_type")
    public static final BlockEntityType<KitchenCabinetBlockEntity> KITCHEN_CABINET = null;
    @ObjectHolder(value = "blockychef:cooking_table", registryName = "block_entity_type")
    public static final BlockEntityType<CookingTableBlockEntity> COOKING_TABLE = null;
    @ObjectHolder(value = "blockychef:kitchen_counter_corner", registryName = "block_entity_type")
    public static final BlockEntityType<KitchenCounterCornerBlockEntity> KITCHEN_COUNTER_CORNER = null;
    @ObjectHolder(value = "blockychef:stove", registryName = "block_entity_type")
    public static final BlockEntityType<StoveBlockEntity> STOVE = null;
    @ObjectHolder(value = "blockychef:dough_maker", registryName = "block_entity_type")
    public static final BlockEntityType<DoughMakerBlockEntity> DOUGH_MAKER = null;
    @ObjectHolder(value = "blockychef:mixer", registryName = "block_entity_type")
    public static final BlockEntityType<MixerBlockEntity> MIXER = null;
    @ObjectHolder(value = "blockychef:toaster", registryName = "block_entity_type")
    public static final BlockEntityType<ToasterBlockEntity> TOASTER = null;
    @ObjectHolder(value = "blockychef:juicer", registryName = "block_entity_type")
    public static final BlockEntityType<JuicerBlockEntity> JUICER = null;
    @ObjectHolder(value = "blockychef:grill", registryName = "block_entity_type")
    public static final BlockEntityType<GrillBlockEntity> GRILL = null;
    @ObjectHolder(value = "blockychef:pasta_machine", registryName = "block_entity_type")
    public static final BlockEntityType<PastaMachineBlockEntity> PASTA_MACHINE = null;
    @ObjectHolder(value = "blockychef:kitchen_sink", registryName = "block_entity_type")
    public static final BlockEntityType<KitchenSinkBlockEntity> KITCHEN_SINK = null;
    @ObjectHolder(value = "blockychef:grater", registryName = "block_entity_type")
    public static final BlockEntityType<GraterBlockEntity> GRATER = null;
    @ObjectHolder(value = "blockychef:cutting_board", registryName = "block_entity_type")
    public static final BlockEntityType<CuttingBoardBlockEntity> CUTTING_BOARD = null;
    @ObjectHolder(value = "blockychef:meat_grinder", registryName = "block_entity_type")
    public static final BlockEntityType<MeatGrinderBlockEntity> MEAT_GRINDER = null;
    @ObjectHolder(value = "blockychef:mortar_and_pestle", registryName = "block_entity_type")
    public static final BlockEntityType<MortarAndPestleBlockEntity> MORTAR_AND_PESTLE = null;
}
