package tnt.blockychef.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.data.fluids.FluidExtraction;
import tnt.tntlib.api.network.Network;
import tnt.tntlib.api.network.message.Server2ClientMessage;

import java.util.ArrayList;
import java.util.List;

@Network.Message(BlockyChef.MODID)
public final class S2C_SendFluidExtractors extends Server2ClientMessage {

    private final List<FluidExtraction> extractionList;

    public S2C_SendFluidExtractors(List<FluidExtraction> extractionList) {
        this.extractionList = extractionList;
    }

    public S2C_SendFluidExtractors(FriendlyByteBuf byteBuf) {
        int size = byteBuf.readInt();
        this.extractionList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.extractionList.add(byteBuf.readJsonWithCodec(FluidExtraction.CODEC));
        }
    }

    @Override
    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(extractionList.size());
        extractionList.forEach(ext -> friendlyByteBuf.writeJsonWithCodec(FluidExtraction.CODEC, ext));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(Minecraft minecraft, CustomPayloadEvent.Context context) {
        BlockyChef.EXTRACTION_MANAGER.loadFromNetwork(extractionList);
    }
}
