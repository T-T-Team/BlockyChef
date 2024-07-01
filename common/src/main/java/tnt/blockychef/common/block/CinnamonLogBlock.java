package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.BlockyChef;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import java.util.List;

public class CinnamonLogBlock extends RegrowingLogBlock {

    public static final ResourceLocation STRIPPING_LOG_LOOT_TABLE = BlockyChef.resource("blocks/cinnamon_log_stripping");

    public CinnamonLogBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if (!simulate && state.getValue(AGE) > 0) {
            Level level = context.getLevel();
            if (!level.isClientSide) {
                dropCinnamonBark(context, (ServerLevel) level);
            }
            return state.setValue(AGE, 0);
        }
        return null;
    }

    @Override
    protected int getDefaultAge() {
        return 3;
    }

    private void dropCinnamonBark(UseOnContext context, ServerLevel level) {
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        LootDataManager lootDataManager = level.getServer().getLootData();
        LootTable table = lootDataManager.getLootTable(STRIPPING_LOG_LOOT_TABLE);
        LootParams params = this.createStrippingLootParams(level, context);
        List<ItemStack> drops = table.getRandomItems(params);
        Vec3 center = pos.getCenter();
        for (ItemStack stack : drops) {
            if (player != null) {
                MenuInventoryHelper.giveItemOrDrop(player, stack);
            } else {
                ItemEntity entity = new ItemEntity(level, center.x, center.y, center.z, stack);
                level.addFreshEntity(entity);
            }

        }
    }

    private LootParams createStrippingLootParams(ServerLevel level, UseOnContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack tool = ctx.getItemInHand();
        Player player = ctx.getPlayer();
        return new LootParams.Builder(level)
                .withParameter(LootContextParams.BLOCK_STATE, state)
                .withParameter(LootContextParams.ORIGIN, pos.getCenter())
                .withParameter(LootContextParams.TOOL, tool)
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .create(LootContextParamSets.BLOCK);
    }
}
