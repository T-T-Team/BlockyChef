package tnt.blockychef.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class CropSeedsItem extends ItemNameBlockItem implements IPlantable {

    public CropSeedsItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public BlockState getPlant(BlockGetter level, BlockPos pos) {
        return this.block.defaultBlockState();
    }

    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.CROP;
    }
}
