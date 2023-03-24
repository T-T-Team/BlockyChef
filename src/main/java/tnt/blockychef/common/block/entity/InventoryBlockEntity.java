package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public abstract class InventoryBlockEntity extends BlockEntity {

    protected IItemHandlerModifiable inventoryHandler;
    private final LazyOptional<IItemHandlerModifiable> inventoryHolder;

    public InventoryBlockEntity(BlockEntityType<? extends InventoryBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventoryHandler = this.setUpInventory();
        this.inventoryHolder = LazyOptional.of(() -> this.inventoryHandler);
    }

    public abstract IItemHandlerModifiable setUpInventory();

    @SuppressWarnings("unchecked")
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (this.serializeInventoryContents()) {
            if (this.inventoryHandler instanceof INBTSerializable<?>) {
                CompoundTag inv = tag.contains("inventory", Tag.TAG_COMPOUND) ? tag.getCompound("inventory") : new CompoundTag();
                ((INBTSerializable<CompoundTag>) this.inventoryHandler).deserializeNBT(inv);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.serializeInventoryContents()) {
            if (this.inventoryHandler instanceof INBTSerializable<?>) {
                CompoundTag inv = ((INBTSerializable<CompoundTag>) this.inventoryHandler).serializeNBT();
                tag.put("inventory", inv);
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.inventoryHolder.cast();
        }
        return super.getCapability(cap, side);
    }

    public IItemHandler getItemHandler() {
        return inventoryHandler;
    }

    protected boolean serializeInventoryContents() {
        return true;
    }
}
