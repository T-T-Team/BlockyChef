package tnt.blockychef.client.render.thirst;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import tnt.blockychef.common.thirst.DrinkProperties;

public final class ThirstTooltipHandler {

    public static void registerTooltipFactory(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ThirstTooltip.class, ThirstTooltipComponent::new);
    }

    public static void gatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        if (event.isCanceled())
            return;
        ItemStack itemStack = event.getItemStack();
        DrinkProperties.DrinkPropertiesHolder holder = DrinkProperties.getDrinkStatistics(itemStack);
        DrinkProperties properties = holder.properties();
        if (!properties.isEmpty()) {
            event.getTooltipElements().add(Either.right(new ThirstTooltip(properties, itemStack)));
        }
    }

    private static class ThirstTooltipComponent implements ClientTooltipComponent {

        private final ThirstTooltip tooltip;

        public ThirstTooltipComponent(ThirstTooltip tooltip) {
            this.tooltip = tooltip;
        }

        @Override
        public int getHeight() {
            return tooltip.renderSaturation ? 20 : 13;
        }

        @Override
        public int getWidth(Font font) {
            int hydration = tooltip.hydrationLevel / 2 * 9;
            if (tooltip.hydrationDescriptor != null) {
                hydration += font.width(tooltip.hydrationDescriptor);
            }
            int saturation = tooltip.saturationLevel / 2 * 7;
            if (tooltip.saturationDescriptor != null) {
                saturation += font.width(tooltip.saturationDescriptor);
            }
            // 2 is used by appleskin, so this should make it look more fitting when both mods are installed
            return Math.max(hydration, saturation) + 2;
        }

        @Override
        public void renderImage(Font font, int x, int y, PoseStack poseStack, ItemRenderer itemRenderer, int z) {
            ItemStack stack = tooltip.stack;
            if (shouldIgnoreRender(stack)) {
                return;
            }
            Minecraft minecraft = Minecraft.getInstance();
            Screen screen = minecraft.screen;
            if (screen == null) {
                return;
            }
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            int offsetX = x;
            int offsetY = y;
            int hydrationValue = Math.abs(tooltip.hydrationLevel);
            boolean negative = tooltip.hydrationLevel < 0;
            offsetX += (Math.abs(tooltip.hydrationLevel) - 1) / 2 * 9;

            // Hydration icons
            RenderSystem.setShaderTexture(0, ThirstOverlay.TEXTURE);
            for (int i = 0; i < hydrationValue; i += 2) {
                GuiComponent.blit(poseStack, offsetX, offsetY, z, negative ? 54 : 0, 0, 9, 9, 256, 256);
                if (i == hydrationValue - 1) {
                    GuiComponent.blit(poseStack, offsetX, offsetY, z, negative ? 72 : 18, 0, 9, 9, 256, 256);
                } else {
                    GuiComponent.blit(poseStack, offsetX, offsetY, z, negative ? 63 : 9, 0, 9, 9, 256, 256);
                }
                offsetX -= 9;
            }
            // Hydration text
            if (tooltip.hydrationDescriptor != null) {
                offsetX += 18;
                poseStack.pushPose();
                poseStack.translate(offsetX, offsetY, z);
                poseStack.scale(0.75F, 0.75F, 0.75F);
                font.drawShadow(poseStack, tooltip.hydrationDescriptor, 2, 2, 0xFFAAAAAA, false);
                poseStack.popPose();
            }

            if (!tooltip.renderSaturation) {
                return;
            }
            // Saturation icons
            RenderSystem.setShaderTexture(0, ThirstOverlay.TEXTURE);
            int saturationValue = tooltip.saturationLevel;
            offsetX = x;
            offsetY += 10;
            offsetX += (tooltip.saturationLevel - 1) / 2 * 7;
            for (int i = 0; i < saturationValue; i += 2) {
                GuiComponent.blit(poseStack, offsetX, offsetY, z, 0, 27, 7, 7, 256, 256);
                float value = (saturationValue - i) / 2.0F;
                if (value >= 1.0F) {
                    GuiComponent.blit(poseStack, offsetX, offsetY, z, 28, 27, 7, 7, 256, 256);
                } else if (value > 0.5F) {
                    GuiComponent.blit(poseStack, offsetX, offsetY, z, 21, 27, 7, 7, 256, 256);
                } else if (value > 0.25F) {
                    GuiComponent.blit(poseStack, offsetX, offsetY, z, 14, 27, 7, 7, 256, 256);
                } else {
                    GuiComponent.blit(poseStack, offsetX, offsetY, z, 7, 27, 7, 7, 256, 256);
                }
                offsetX -= 7;
            }
            // Saturation text
            if (tooltip.saturationDescriptor != null) {
                offsetX += 14;
                poseStack.pushPose();
                poseStack.translate(offsetX, offsetY, z);
                poseStack.scale(0.75f, 0.75f, 0.75f);
                font.drawShadow(poseStack, tooltip.saturationDescriptor, 2, 1, 0xFFAAAAAA, false);
                poseStack.popPose();
            }
        }

        private static boolean shouldIgnoreRender(ItemStack stack) {
            return stack.isEmpty() || DrinkProperties.NONE_HOLDER == DrinkProperties.getDrinkStatistics(stack);
        }
    }

    private static class ThirstTooltip implements TooltipComponent {

        private final ItemStack stack;
        private int hydrationLevel;
        private int saturationLevel;
        private String hydrationDescriptor;
        private String saturationDescriptor;
        private boolean renderSaturation = true;

        public ThirstTooltip(DrinkProperties properties, ItemStack stack) {
            this.stack = stack;

            this.hydrationLevel = properties.getHydration();
            if (Math.abs(this.hydrationLevel) > 20) {
                this.hydrationLevel = 1;
                this.hydrationDescriptor = "x" + properties.getHydration();
            }

            this.saturationLevel = Mth.ceil(properties.getHydration() * properties.getSaturation() * 2.0F);
            if (saturationLevel > 20.0F) {
                this.saturationDescriptor = "x" + saturationLevel;
                this.saturationLevel = 1;
            } else if (this.saturationLevel <= 0.0F) {
                this.renderSaturation = false;
            }
        }
    }
}
