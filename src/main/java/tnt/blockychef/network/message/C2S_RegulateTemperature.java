package tnt.blockychef.network.message;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.client.BlockyChefClient;
import tnt.blockychef.common.heat.HeatHelper;
import tnt.blockychef.common.heat.HeatSource;
import tnt.blockychef.common.heat.RegulatedHeatSource;
import tnt.blockychef.common.heat.RegulationHandler;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.network.Network;
import tnt.tntlib.api.network.message.Client2ServerMessage;

import javax.annotation.Nullable;

@Network.Message(BlockyChef.MODID)
public class C2S_RegulateTemperature extends Client2ServerMessage {

    private final BlockPos position;
    @Nullable
    private final Direction direction;
    private final boolean decreasing;
    private final float amount;

    @OnlyIn(Dist.CLIENT)
    public C2S_RegulateTemperature(BlockPos position, @Nullable Direction direction, boolean decreasing, float amount) {
        this.position = position;
        this.direction = direction;
        this.decreasing = decreasing;
        this.amount = amount;
    }

    @OnlyIn(Dist.CLIENT)
    public C2S_RegulateTemperature(BlockPos position, @Nullable Direction direction, boolean decreasing) {
        this(position, direction, decreasing, BlockyChefClient.CLIENT.config.getTemperatureStepAmount());
    }

    public C2S_RegulateTemperature(FriendlyByteBuf buffer) {
        this.position = buffer.readBlockPos();
        this.direction = buffer.readBoolean() ? buffer.readEnum(Direction.class) : null;
        this.decreasing = buffer.readBoolean();
        this.amount = buffer.readFloat();
    }

    @Override
    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(position);
        friendlyByteBuf.writeBoolean(direction != null);
        if (direction != null) {
            friendlyByteBuf.writeEnum(direction);
        }
        friendlyByteBuf.writeBoolean(decreasing);
        friendlyByteBuf.writeFloat(amount);
    }

    @Override
    public void handle(ServerPlayer serverPlayer, CustomPayloadEvent.Context context) {
        ServerLevel level = serverPlayer.serverLevel();
        HeatSource source = HeatHelper.getHeatSource(level, position, direction);
        if (source instanceof RegulatedHeatSource regulatedHeatSource) {
            BlockEntity blockEntity = level.getBlockEntity(position);
            RegulationHandler handler = regulatedHeatSource.getRegulationHandler();
            if (decreasing) {
                handler.decrease(amount);
            } else {
                handler.increase(amount);
            }
            if (blockEntity instanceof Synchronizable) {
                BlockEntityHelper.sendBlockEntityClientData((BlockEntity & Synchronizable) blockEntity);
            }
        }
    }
}
