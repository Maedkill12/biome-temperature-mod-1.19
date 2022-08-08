package net.maed.biomestemperature.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.maed.biomestemperature.BiomesTemperature;
import net.maed.biomestemperature.biome.WorldTemperatureManager;
import net.maed.biomestemperature.config.Configs;
import net.maed.biomestemperature.util.IPlayerData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.text.DecimalFormat;

public class ModInGameHud extends DrawableHelper {
    private static final Identifier HEAT_OUTLINE = new Identifier(BiomesTemperature.MOD_ID, "textures/heat/heat_outline.png");
    private static final Identifier THERMOMETER = new Identifier(BiomesTemperature.MOD_ID, "textures/heat/thermometer.png");
    private static final Identifier THERMOMETER_COLD = new Identifier(BiomesTemperature.MOD_ID, "textures/heat/thermometer_cold.png");
    private static final Identifier THERMOMETER_HEAT = new Identifier(BiomesTemperature.MOD_ID, "textures/heat/thermometer_heat.png");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    private final Random random = Random.create();
    private final MinecraftClient client;
    private int scaledWidth;
    private int scaledHeight;
    private int renderHealthValue;
    private int lastHealthValue;
    private long lastHealthCheckTime;
    private int ticks;
    private int heartJumpEndTick;

    public ModInGameHud(MinecraftClient client) {
        this.client = client;
    }

    public void render(MatrixStack matrices, float tickDelta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        this.scaledWidth = this.client.getWindow().getScaledWidth();
        this.scaledHeight = this.client.getWindow().getScaledHeight();
        IPlayerData playerData = (IPlayerData) client.player;
        if (playerData.getCustomHeatTick() > 0) {
            this.renderOverlay(HEAT_OUTLINE, playerData.getHeatScale());
        }

        if (!this.client.options.hudHidden) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
            if (this.client.interactionManager.hasStatusBars()) {
                this.renderStatusBar(matrices);
                this.renderThermometer(matrices, playerData);
            }
        }
    }

    private void renderStatusBar(MatrixStack matrices) {
        PlayerEntity playerEntity = this.getCameraPlayer();

        int i = MathHelper.ceil(playerEntity.getHealth());
        boolean bl = this.heartJumpEndTick > (long)this.ticks && (this.heartJumpEndTick - (long)this.ticks) / 3L % 2L == 1L;
        long l = Util.getMeasuringTimeMs();
        if (i < this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
            this.lastHealthCheckTime = l;
            this.heartJumpEndTick = this.ticks + 10;
        } else if (i > this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
            this.lastHealthCheckTime = l;
            this.heartJumpEndTick = this.ticks + 20;
        }
        if (l - this.lastHealthCheckTime > 1000L) {
            this.renderHealthValue = i;
            this.lastHealthCheckTime = l;
        }
        this.lastHealthValue = i;
        int j = this.renderHealthValue;
        int m = this.scaledWidth / 2 - 91;
        int o = this.scaledHeight - 39;
        float f = Math.max((float)playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float)Math.max(j, i));
        int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
        int q = MathHelper.ceil((f + (float)p) / 2.0f / 10.0f);
        int r = Math.max(10 - (q - 2), 3);
        int v = -1;
        if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
            v = this.ticks % MathHelper.ceil(f + 5.0f);
        }
        this.renderHealthBar(matrices, playerEntity, m, o, r, v, f, i, j, p, bl);
    }

    private void renderHealthBar(MatrixStack matrices, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking) {
        HeartType heartType = HeartType.fromPlayerState((IPlayerData) player);
        if (heartType == null || player.hasStatusEffect(StatusEffects.POISON) || player.hasStatusEffect(StatusEffects.WITHER)) {
            return;
        }
        int i = 9 * (player.world.getLevelProperties().isHardcore() ? 5 : 0);
        int j = MathHelper.ceil((double)maxHealth / 2.0);
        int k = MathHelper.ceil((double)absorption / 2.0);
        for (int m = j + k - 1; m >= 0; --m) {
            boolean bl3;
            int n = m / 10;
            int o = m % 10;
            int p = x + o * 8;
            int q = y - n * lines;
            if (lastHealth + absorption <= 4) {
                q += this.random.nextInt(2);
            }
            if (m < j && m == regeneratingHeartIndex) {
                q -= 2;
            }
            this.drawHeart(matrices, HeartType.CONTAINER, p, q, i, blinking, false);
            int r = m * 2;

            if (blinking && r < health) {
                bl3 = r + 1 == health;
                this.drawHeart(matrices, heartType, p, q, i, true, bl3);
            }
            if (r >= lastHealth) continue;
            bl3 = r + 1 == lastHealth;
            this.drawHeart(matrices, heartType, p, q, i, false, bl3);
        }
    }

    private void drawHeart(MatrixStack matrices, HeartType type, int x, int y, int v, boolean blinking, boolean halfHeart) {
        this.drawTexture(matrices, x, y, type.getU(halfHeart, blinking), v, 9, 9);
    }

    private void renderThermometer(MatrixStack matrices, IPlayerData playerData) {
        int scale = 2;
        int x = this.scaledWidth - 32 * scale;
        int y = this.scaledHeight  - 48 * scale;
        if (playerData.getCustomHeatTick() > 0) {
            RenderSystem.setShaderTexture(0, THERMOMETER_HEAT);
        } else if (playerData.getCustomFrozenTick() > 0) {
            RenderSystem.setShaderTexture(0, THERMOMETER_COLD);
        } else {
            RenderSystem.setShaderTexture(0, THERMOMETER);
        }

        this.drawTexture(matrices, x, y, 0, 0, 16 * scale, 32 * scale, 16 * scale, 32 * scale);

        String formatted = "";
        switch (Configs.TEMPERATURE_SCALE) {
            case 1:
                formatted = DECIMAL_FORMAT.format(celsiusToFahrenheit(WorldTemperatureManager.currentAmbientTemperature)) + " °F";
                break;
            case 2:
                formatted = DECIMAL_FORMAT.format(celsiusToKelvin(WorldTemperatureManager.currentAmbientTemperature)) + " K";
                break;
            default:
                formatted = DECIMAL_FORMAT.format(WorldTemperatureManager.currentAmbientTemperature) + " °C";
        }

        Text text = Text.literal(formatted);
        this.client.textRenderer.drawWithShadow(matrices, text, x + 10, y + 64, 0xFFFFFF);
    }

    private PlayerEntity getCameraPlayer() {
        if (!(this.client.getCameraEntity() instanceof PlayerEntity)) {
            return null;
        }
        return (PlayerEntity)this.client.getCameraEntity();
    }

    public void tick(boolean paused) {
        if (!paused) {
            this.tick();
        }
    }

    private void tick() {
        ++this.ticks;
    }

    private void renderOverlay(Identifier texture, float opacity) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, opacity);
        RenderSystem.setShaderTexture(0, texture);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(0.0, this.scaledHeight, -90.0).texture(0.0f, 1.0f).next();
        bufferBuilder.vertex(this.scaledWidth, this.scaledHeight, -90.0).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(this.scaledWidth, 0.0, -90.0).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0f, 0.0f).next();
        tessellator.draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private float celsiusToFahrenheit(float temp) {
        return temp * 9 / 5 + 32;
    }

    private float celsiusToKelvin(float temp) {
        return temp + 273.15f;
    }

    enum HeartType  {
        CONTAINER(0, false),
        HEAT(5, false);

        private final int textureIndex;
        private final boolean hasBlinkingTexture;

        HeartType(int textureIndex, boolean hasBlinkingTexture) {
            this.textureIndex = textureIndex;
            this.hasBlinkingTexture = hasBlinkingTexture;
        }

        public int getU(boolean halfHeart, boolean blinking) {
            int i;
            if (this == CONTAINER) {
                i = blinking ? 1 : 0;
            } else {
                int j = halfHeart ? 1 : 0;
                int k = this.hasBlinkingTexture && blinking ? 2 : 0;
                i = j + k;
            }
            return 16 + (this.textureIndex * 2 + i) * 9;
        }

        static HeartType fromPlayerState(IPlayerData playerData) {
            HeartType heartType = playerData.hasHeat() ? HEAT : null;
            return heartType;
        }
    }
}
