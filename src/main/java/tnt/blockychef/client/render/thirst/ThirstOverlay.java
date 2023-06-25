package tnt.blockychef.client.render.thirst;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.lwjgl.opengl.GL11;
import tnt.blockychef.common.init.BlockyChefMobEffects;
import tnt.blockychef.common.thirst.DrinkProperties;
import tnt.blockychef.common.thirst.PlayerThirstStatsProvider;
import tnt.blockychef.common.thirst.ThirstStats;
import tnt.blockychef.integrations.Integrations;

public class ThirstOverlay implements IGuiOverlay {

    public static final ResourceLocation TEXTURE = new ResourceLocation("blockychef:textures/icon/hydration_icons.png");
    private static int fadeMultiplier = 1;
    private static float alpha;
    private static float lastAlpha;
    private static float alphaUnmodified;
    private final RandomSource random = RandomSource.create();
    private final OverlayRenderer renderer;

    public ThirstOverlay() {
        this.renderer = Integrations.shouldRenderFancyOverlay() ? this::renderFancy : this::renderDefault;
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = gui.getMinecraft();
        minecraft.getProfiler().push("blockychef:thirst");
        Player player = (Player) minecraft.getCameraEntity();

        boolean isMounted = player.getVehicle() instanceof LivingEntity;
        if (!(!isMounted && !gui.getMinecraft().options.hideGui && gui.shouldDrawSurvivalElements())) {
            return;
        }

        setupRender();
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        renderer.renderOverlay(player, gui, graphics, partialTick, screenWidth, screenHeight);
        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }

    private static void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
    }

    private static void setAlpha(float alpha) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
    }

    private static void resetAlpha() {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    private void renderFancy(Player player, ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        int left = screenWidth / 2 + 91;
        int top = screenHeight - gui.rightHeight;
        gui.rightHeight += 10;
        boolean isThirsty = player.hasEffect(BlockyChefMobEffects.THIRST);
        player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> {
            renderExhaustion(graphics, stats.getExhaustionLevel(), left, top);
            setupRender();
            float smoothAlpha = lastAlpha + (alpha - lastAlpha) * partialTick;
            renderHeldItemStats(graphics, gui, player, stats, isThirsty, left, top, smoothAlpha);
        });
    }

    private void renderDefault(Player player, ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        int left = screenWidth / 2 + 91;
        int top = screenHeight - gui.rightHeight;
        gui.rightHeight += 10;
        boolean thirsty = player.hasEffect(BlockyChefMobEffects.THIRST);
        player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> {
            int hydration = stats.getHydrationLevel();
            float saturation = stats.getSaturationLevel();
            renderHydrationOverlay(graphics, gui, hydration, 0, saturation, thirsty, left, top, 1.0F);
        });
    }

    private void renderHydrationOverlay(GuiGraphics graphics, ForgeGui gui, int hydrationLevel, int extraHydration, float saturationLevel, boolean thirsty, int left, int top, float alpha) {
        boolean isLoss = extraHydration < 0;
        int totalHydration = hydrationLevel + Math.abs(extraHydration);
        int playerHydration = Math.min(hydrationLevel, hydrationLevel + extraHydration);
        int j = 0;
        random.setSeed(gui.getGuiTicks() * 312871L);
        for (int i = 0; i < 10; ++i) {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;

            if (saturationLevel <= 0.0F && gui.getGuiTicks() % (hydrationLevel * 3 + 1) == 0) {
                y = top + (random.nextInt(3) - 1);
            }

            // Current hydration
            graphics.blit(TEXTURE, x, y, 0, thirsty ? 27 : 0, 0, 9, 9, 256, 256);
            if (idx < hydrationLevel) {
                graphics.blit(TEXTURE, x, y, 0, thirsty ? 36 : 9, 0, 9, 9, 256, 256);
                if (idx <= playerHydration) {
                    j = i;
                }
            } else if (idx == hydrationLevel) {
                graphics.blit(TEXTURE, x, y, 0, thirsty ? 45 : 18, 0, 9, 9, 256, 256);
                if (idx <= playerHydration) {
                    j = i;
                }
            }

            if (!isLoss) {
                setAlpha(alpha);
                if (idx < totalHydration) {
                    graphics.blit(TEXTURE, x, y, 0, thirsty ? 36 : 9, 0, 9, 9, 256, 256);
                } else if (idx == totalHydration) {
                    graphics.blit(TEXTURE, x, y, 0, thirsty ? 45 : 18, 0, 9, 9, 256, 256);
                }
                resetAlpha();
            }
        }
        if (!isLoss) {
            return;
        }
        random.setSeed(gui.getGuiTicks() * 312871L);
        for (int i = 9; i >= j; --i) {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;

            if (saturationLevel <= 0.0F && gui.getGuiTicks() % (hydrationLevel * 3 + 1) == 0) {
                y = top + (random.nextInt(3) - 1);
            }

            setAlpha(alpha);
            if (idx < hydrationLevel) {
                if (i == j && idx == playerHydration) {
                    graphics.blit(TEXTURE, x, y, 0, 81, 0, 9, 9, 256, 256);
                } else if (i > j) {
                    graphics.blit(TEXTURE, x, y, 0, 63, 0, 9, 9, 256, 256);
                }
            } else if (idx == hydrationLevel) {
                graphics.blit(TEXTURE, x, y, 0, 72, 0, 9, 9, 256, 256);
            }
            resetAlpha();
        }
    }

    private void renderSaturation(GuiGraphics graphics, ForgeGui gui, int hydration, float saturation, boolean thirsty, int left, int top, float alpha) {
        int intSat = Mth.ceil(Math.min(saturation, 20.0F) / 2.0F);
        random.setSeed(gui.getGuiTicks() * 312871L);
        for (int i = 0; i < intSat; i++) {
            int x = left - i * 8 - 9;
            int y = top;
            if (saturation <= 0.0F && gui.getGuiTicks() % (hydration * 3 + 1) == 0) {
                y = top + (random.nextInt(3) - 1);
            }
            float value = (saturation / 2.0F) - i;
            int icon = 0;
            if (value >= 1.0F)
                icon = 3;
            else if (value > 0.5F)
                icon = 2;
            else if (value > 0.25F)
                icon = 1;
            if (thirsty) {
                icon += 4;
            }
            setAlpha(alpha);
            graphics.blit(TEXTURE, x, y, 0, icon * 9, 9, 9, 9, 256, 256);
            resetAlpha();
        }
    }

    private void renderExhaustion(GuiGraphics graphics, float exhaustion, int left, int top) {
        float value = exhaustion / 4.0F;
        setAlpha(0.75F);
        int xSize = (int) (value * 81);
        graphics.blit(TEXTURE, left - xSize, top, left, top + 9, 81 - xSize, 18, 81, 27, 256, 256);
        resetAlpha();
    }

    private void renderHeldItemStats(GuiGraphics graphics, ForgeGui gui, Player player, ThirstStats stats, boolean thirsty, int left, int top, float alpha) {
        ItemStack itemStack = player.getMainHandItem();
        DrinkProperties properties = DrinkProperties.getDrinkStatistics(itemStack).properties();
        int playerHydration = stats.getHydrationLevel();
        float playerSaturation = stats.getSaturationLevel();
        if (properties.isEmpty()) {
            renderHydrationOverlay(graphics, gui, playerHydration, 0, playerSaturation, thirsty, left, top, 1.0F);
            renderSaturation(graphics, gui, playerHydration, playerSaturation, thirsty, left, top, 1.0F);
            return;
        }
        int itemHydration = properties.getHydration();
        float itemSaturation = properties.getSaturation();
        renderHydrationOverlay(graphics, gui, playerHydration, itemHydration, playerSaturation, thirsty, left, top, alpha);
        renderSaturation(graphics, gui, playerHydration, playerSaturation, thirsty, left, top, 1.0F);
        float addedSaturation = Math.min(playerHydration + itemHydration, playerSaturation + (itemHydration * itemSaturation * 2.0F));
        renderSaturation(graphics, gui, playerHydration, addedSaturation, thirsty, left, top, alpha);
    }

    public static void tick() {
        lastAlpha = alpha;
        alphaUnmodified += fadeMultiplier * 0.125F;
        if (alphaUnmodified >= 1.5F) {
            fadeMultiplier = -1;
        } else if (alphaUnmodified <= -0.5F) {
            fadeMultiplier = 1;
        }
        alpha = Mth.clamp(alphaUnmodified, 0.0F, 1.0F) * Integrations.getAlphaForHudHydrationOverlay();
    }

    @FunctionalInterface
    private interface OverlayRenderer {
        void renderOverlay(Player player, ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight);
    }
}
