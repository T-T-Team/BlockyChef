package tnt.blockychef.integrations.waila.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.ui.Element;
import snownee.jade.overlay.DisplayHelper;
import tnt.blockychef.common.fluid.EdibleFluidType;

import java.util.Objects;

public class FluidStackElementExt extends Element {

    private final JadeFluidObject fluid;

    public FluidStackElementExt(FluidStack stack) {
        this.fluid = JadeFluidObject.of(stack.getFluid(), stack.getAmount());
        Objects.requireNonNull(fluid);
        Component fluidLabel = Component.translatable(fluid.getType().getFluidType().getDescriptionId());
        message = fluidLabel.getString();
    }

    @Override
    public Vec2 getSize() {
        return new Vec2(18 + 3 + Minecraft.getInstance().font.width(message), 18);
    }

    @Override
    public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
        Vec2 size = this.getCachedSize();
        DisplayHelper.INSTANCE.drawFluid(guiGraphics, x, y, this.fluid, 18.0F, size.y, JadeFluidObject.bucketVolume());
        int fluidColor = fluid.getType().getFluidType() instanceof EdibleFluidType edible ? edible.getFluidColor() : 0xFFFFFF;
        DisplayHelper.INSTANCE.drawText(guiGraphics, getCachedMessage(), x + size.y + 3, y, fluidColor);
        DisplayHelper.INSTANCE.drawText(guiGraphics, Component.translatable("label.blockychef.fluid_amount", fluid.getAmount()), x + size.y + 3, y + 10, fluidColor);
    }
}
