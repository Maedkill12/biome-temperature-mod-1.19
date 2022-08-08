package net.maed.biomestemperature.client.gui.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.maed.biomestemperature.ModClient;
import net.minecraft.client.util.math.MatrixStack;

public class HudOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        ModClient.modInGameHud.render(matrixStack, tickDelta);
    }
}
