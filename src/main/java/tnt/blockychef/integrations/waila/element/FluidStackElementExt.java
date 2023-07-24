package tnt.blockychef.integrations.waila.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.ui.Element;
import snownee.jade.overlay.DisplayHelper;

import java.util.Objects;

public class FluidStackElementExt extends Element {

    private final JadeFluidObject fluid;

    public FluidStackElementExt(FluidStack stack) {
        this.fluid = JadeFluidObject.of(stack.getFluid(), stack.getAmount());
        Objects.requireNonNull(fluid);
        message = Component.translatable(fluid.getType().getFluidType().getDescriptionId(), fluid.getAmount()).getString();
    }

    @Override
    public Vec2 getSize() {
        return new Vec2(16 + 3 + Minecraft.getInstance().font.width(message), 16);
    }

    @Override
    public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
        Vec2 size = this.getCachedSize();
        DisplayHelper.INSTANCE.drawFluid(guiGraphics, x, y, this.fluid, 16.0F, size.y, JadeFluidObject.bucketVolume());
        DisplayHelper.INSTANCE.drawText(guiGraphics, getCachedMessage(), x + size.y + 3, y + (size.y - 9) / 2f, 0xffffff);
    }
}
